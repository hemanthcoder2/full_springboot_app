package com.yourapp.pricing_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RazorpayOrderResponse {
    private Long subscriptionId;
    private String orderId;
    private Double amount;
    private String currency;
    private Long companyId;


}
