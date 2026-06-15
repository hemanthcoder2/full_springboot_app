package com.yourapp.pricing_service.config;

import com.yourapp.pricing_service.entity.Plan;
import com.yourapp.pricing_service.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {
    private final PlanRepository planRepository;

    @Override
    public void run(String... args){
        if(planRepository.count()==0){
            planRepository.save(Plan.builder()
                            .name("Free Trial")
                            .description("7 days free trial")
                            .price(0.0)
                            .billingCycle(Plan.BillingCycle.FREE_TRIAL)
                            .durationDays(7)
                            .gracePeriodDays(0)
                    .build());
            planRepository.save(Plan.builder()
                    .name("Monthly")
                    .description("Monthly plan")
                    .price(999.0)
                    .billingCycle(Plan.BillingCycle.MONTHLY)
                    .durationDays(30)
                    .gracePeriodDays(3)
                    .build());

            planRepository.save(Plan.builder()
                    .name("Quarterly")
                    .description("Quarterly plan")
                    .price(2499.0)
                    .billingCycle(Plan.BillingCycle.QUARTERLY)
                    .durationDays(90)
                    .gracePeriodDays(5)
                    .build());

            planRepository.save(Plan.builder()
                    .name("Half Yearly")
                    .description("Half yearly plan")
                    .price(4499.0)
                    .billingCycle(Plan.BillingCycle.HALF_YEARLY)
                    .durationDays(180)
                    .gracePeriodDays(7)
                    .build());

            planRepository.save(Plan.builder()
                    .name("Yearly")
                    .description("Yearly plan")
                    .price(7999.0)
                    .billingCycle(Plan.BillingCycle.YEARLY)
                    .durationDays(365)
                    .gracePeriodDays(15)
                    .build());

            log.info("Plans seeded successfully");
        }
    }

}
