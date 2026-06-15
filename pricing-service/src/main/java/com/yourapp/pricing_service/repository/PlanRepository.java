package com.yourapp.pricing_service.repository;

import com.yourapp.pricing_service.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan,Long> {
    List<Plan> findByIsActiveTrue();
    Optional<Plan> findByBillingCycle(Plan.BillingCycle billingCycle);

}
