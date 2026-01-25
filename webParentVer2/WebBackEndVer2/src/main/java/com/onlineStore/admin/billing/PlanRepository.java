package com.onlineStore.admin.billing;

import com.onlineStoreCom.entity.billing.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
    Plan findByName(String name);
}
