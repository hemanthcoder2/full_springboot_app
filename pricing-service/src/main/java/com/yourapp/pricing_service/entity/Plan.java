package com.yourapp.pricing_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="plans")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;


    private String description;

    @Column(nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BillingCycle billingCycle = BillingCycle.FREE_TRIAL;

    @Column(nullable = false)
    private int durationDays;

    @Builder.Default
    private int gracePeriodDays = 0;

    @Builder.Default
    private boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum BillingCycle{
        FREE_TRIAL, MONTHLY, QUARTERLY, HALF_YEARLY, YEARLY
    }
}
