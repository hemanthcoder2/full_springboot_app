package com.yourapp.pricing_service.repository;

import com.yourapp.pricing_service.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {
    Optional<Subscription> findByCompanyId(Long companyId);
    List<Subscription> findByStatus(Subscription.SubscriptionStatus status);
}
