package com.yourapp.pricing_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {
    private Long id;
    private Long companyId;
    private Long planId;
    private String planName;
    private String status;
    private String startDate;
    private String endDate;
    private String gracePeriodEndDate;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private int daysRemaining;
    private String createdAt;
}
