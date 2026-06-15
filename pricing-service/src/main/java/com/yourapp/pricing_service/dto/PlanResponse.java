package com.yourapp.pricing_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String billingCycle;
    private int durationDays;
    private int gracePeriodDays;
    private boolean active;

}
