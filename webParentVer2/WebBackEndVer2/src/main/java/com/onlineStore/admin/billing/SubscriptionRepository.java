package com.onlineStore.admin.billing;

import com.onlineStoreCom.entity.billing.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    // Find subscription for a specific tenant (Target Tenant)
    Optional<Subscription> findByTargetTenantId(Long targetTenantId);
}
