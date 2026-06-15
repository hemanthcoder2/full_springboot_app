package com.yourapp.pricing_service.dto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubscriptionRequest {
    @NotNull(message = "company id is required")
    private Long companyId;
    @NotNull(message = "plan id is required")
    private Long planId;
}
