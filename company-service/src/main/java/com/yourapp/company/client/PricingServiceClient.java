package com.yourapp.company.client;

import com.yourapp.company.dto.CompanyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PricingServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.pricing-url}")
    private String pricingServiceUrl;

    /**
     * Calls POST /api/v1/subscriptions on the Pricing Service.
     * Attaches the selected plan (or free trial) to the new company.
     */
    public void createSubscription(Long companyId, Long planId) {
        String url = pricingServiceUrl + "/api/v1/subscriptions";

        CompanyDto.CreateSubscriptionRequest request =
            CompanyDto.CreateSubscriptionRequest.builder()
                .companyId(companyId)
                .planId(planId)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CompanyDto.CreateSubscriptionRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(url, entity, Void.class);
            log.info("Subscription created for company {} on plan {}", companyId, planId);
        } catch (Exception e) {
            // Non-fatal — log warning. Company is already created.
            // A background job or retry can fix this later.
            log.warn("Pricing Service unavailable when creating subscription for company {}: {}",
                companyId, e.getMessage());
        }
    }
}
