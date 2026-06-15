package com.yourapp.pricing_service.controller;

import com.yourapp.pricing_service.dto.ApiResponse;
import com.yourapp.pricing_service.dto.CreateSubscriptionRequest;
import com.yourapp.pricing_service.dto.PaymentVerificationRequest;
import com.yourapp.pricing_service.service.PricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PricingController {

    private final PricingService pricingService;

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse> getPlans(){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("plans",pricingService.getPlans())
        );
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<ApiResponse> createSubscription(@Valid @RequestBody CreateSubscriptionRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Subscription created", pricingService.createSubscription(request))
        );
    }

    @GetMapping("/subscriptions/status")
    public ResponseEntity<ApiResponse> getSubscriptionStatus(@RequestHeader("X-Company-Id") Long companyId){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("subscription status", pricingService.getSubscriptionStatus(companyId))
        );
    }

    @PostMapping("/subscriptions/initiate-payment")
    public ResponseEntity<ApiResponse> initiatePayment(
            @RequestHeader("X-Company-Id") Long companyId, @RequestParam Long planId){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        "Initiated payment", pricingService.initiatePayment(companyId,planId))
        );
    }

    @PostMapping("/subscriptions/verify-payment")
    public ResponseEntity<ApiResponse> verifyPayment(@Valid @RequestBody PaymentVerificationRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        "Verified Payment",pricingService.verifyPayment(request)
                )
        );
    }
}
