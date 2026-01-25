package com.onlineStore.admin.billing;

import com.onlineStoreCom.entity.billing.BillingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingTransactionRepository extends JpaRepository<BillingTransaction, Integer> {

    List<BillingTransaction> findBySourceTenantId(Long sourceTenantId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(t.amount) FROM BillingTransaction t")
    Double sumTotalAmount();
}
