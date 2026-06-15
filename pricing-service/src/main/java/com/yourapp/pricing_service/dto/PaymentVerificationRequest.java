package com.yourapp.pricing_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVerificationRequest {

    @NotNull(message = "subscription id is required")
    private Long subscriptionId;
    @NotBlank(message = "razor pay id is required")
    private String razorpayOrderId;
    @NotBlank(message = "razor payment id is required")
    private String razorpayPaymentId;
    @NotBlank(message = "razor pay signature id is required")
    private String razorpaySignature;
}
