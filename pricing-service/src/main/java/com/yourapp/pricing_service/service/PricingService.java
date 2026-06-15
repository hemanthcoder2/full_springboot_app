package com.yourapp.pricing_service.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.yourapp.pricing_service.config.RazorpayConfig;
import com.yourapp.pricing_service.dto.*;
import com.yourapp.pricing_service.entity.Plan;
import com.yourapp.pricing_service.entity.Subscription;
import com.yourapp.pricing_service.repository.PlanRepository;
import com.yourapp.pricing_service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import com.yourapp.pricing_service.dto.RazorpayOrderResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final RazorpayConfig razorpayConfig;



    public SubscriptionResponse createSubscription(CreateSubscriptionRequest request){
        Plan plan = planRepository.findById(request.getPlanId()).orElseThrow(
                ()->new RuntimeException("Plan not found")
        );

        if(subscriptionRepository.findByCompanyId(request.getCompanyId()).isPresent()) {
            throw new RuntimeException("Company already has a subscription");
        }
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(plan.getDurationDays());
        LocalDate gracePeriodEndDate = endDate.plusDays(plan.getGracePeriodDays());
        Subscription subscription = Subscription.builder()
                .companyId(request.getCompanyId())
                .planId(request.getPlanId())
                .status(Subscription.SubscriptionStatus.TRIAL)
                .startDate(startDate)
                .endDate(endDate)
                .gracePeriodEndDate(gracePeriodEndDate)
                .build();

        subscription = subscriptionRepository.save(subscription);

        return toSingleResponse(subscription, plan);

    }

    public List<PlanResponse> getPlans() {
        return planRepository.findByIsActiveTrue()
                .stream()
                .map(this::toPlanResponse)
                .toList();
    }
    public SubscriptionResponse getSubscriptionStatus(Long companyId){
        Subscription subscription = subscriptionRepository.findByCompanyId(companyId)
                .orElseThrow(()->new RuntimeException("Subscription not found"));
        if(LocalDate.now().isAfter(subscription.getGracePeriodEndDate())){
            subscription.setStatus(Subscription.SubscriptionStatus.EXPIRED);
        }
        long daysRemaining = 0;
        if (subscription.getEndDate() != null) {
            daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), subscription.getEndDate());
            if (daysRemaining < 0) daysRemaining = 0;
        }

        subscription = subscriptionRepository.save(subscription);
        Plan plan = planRepository.findById(subscription.getPlanId()).orElse(null);
        return toSingleResponse(subscription,plan);
    }
    public RazorpayOrderResponse initiatePayment(Long companyId, Long planId){
        Subscription subscription = subscriptionRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        try{
            RazorpayClient client = new RazorpayClient(razorpayConfig.getKeyId(), razorpayConfig.getKeySecret());
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int)(plan.getPrice() * 100)); // convert to paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "sub_" + subscription.getId());
            Order order = client.orders.create(orderRequest);
            subscription.setRazorpayOrderId(order.get("id"));
            subscriptionRepository.save(subscription);
            return RazorpayOrderResponse.builder()
                    .subscriptionId(subscription.getId())
                    .orderId(order.get("id"))
                    .amount(plan.getPrice())
                    .currency("INR")
                    .companyId(companyId)
                    .build();

        }catch (RazorpayException e){
            log.error("Razorpay order creation failed: {}", e.getMessage());
            throw new RuntimeException("Payment initiation failed");

        }
    }

    public SubscriptionResponse verifyPayment(PaymentVerificationRequest request){
        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        Plan plan = planRepository.findById(subscription.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        try {
            String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
            String generatedSignature = calculateHMAC(payload, razorpayConfig.getKeySecret());
            if (!generatedSignature.equals(request.getRazorpaySignature())) {
                throw new RuntimeException("Payment verification failed — invalid signature");
            }
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusDays(plan.getDurationDays());
            LocalDate gracePeriodEndDate = endDate.plusDays(plan.getGracePeriodDays());

            subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
            subscription.setRazorpayPaymentId(request.getRazorpayPaymentId());
            subscription.setStartDate(startDate);
            subscription.setEndDate(endDate);
            subscription.setGracePeriodEndDate(gracePeriodEndDate);

            subscription = subscriptionRepository.save(subscription);
            return toSingleResponse(subscription, plan);

        }catch (Exception ex){
            log.error("Payment verification failed: {}", ex.getMessage());
            throw new RuntimeException("Payment verification failed");

        }

    }

    private String calculateHMAC(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException("HMAC calculation failed", e);
        }
    }
    private PlanResponse toPlanResponse(Plan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .billingCycle(plan.getBillingCycle().name())
                .durationDays(plan.getDurationDays())
                .gracePeriodDays(plan.getGracePeriodDays())
                .active(plan.isActive())
                .build();
    }



    private SubscriptionResponse toSingleResponse(Subscription subscription, Plan plan) {
        long daysRemaining = 0;
        if (subscription.getEndDate() != null) {
            daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), subscription.getEndDate());
            if (daysRemaining < 0) daysRemaining = 0;
        }

        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .companyId(subscription.getCompanyId())
                .planId(subscription.getPlanId())
                .planName(plan!=null? plan.getName() : null)
                .status(subscription.getStatus().name())
                .startDate(subscription.getStartDate() != null ? subscription.getStartDate().toString():null)
                .endDate(subscription.getEndDate() != null ? subscription.getEndDate().toString():null)
                .gracePeriodEndDate(subscription.getGracePeriodEndDate()!=null ? subscription.getGracePeriodEndDate().toString():null)
                .razorpayOrderId(subscription.getRazorpayOrderId())
                .razorpayPaymentId(subscription.getRazorpayPaymentId())
                .daysRemaining((int) daysRemaining)
                .createdAt(subscription.getCreatedAt()!=null?subscription.getCreatedAt().toString():null)
                .build();
    }
}