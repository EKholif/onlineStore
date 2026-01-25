package com.onlineStore.admin.billing;

import com.onlineStoreCom.entity.billing.*;
import com.onlineStoreCom.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class BillingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingService.class);

    @Autowired
    private PlanRepository planRepo;

    @Autowired
    private SubscriptionRepository subscriptionRepo;

    @Autowired
    private BillingTransactionRepository transactionRepo;

    /**
     * Create a new Plan (Platform Admin Only - Tenant 0)
     */
    public Plan createPlan(Plan plan) {
        verifyRootAccess("createPlan");
        return planRepo.save(plan);
    }

    /**
     * Subscribe a tenant to a plan.
     * This creates a Subscription entity in the ROOT context (Tenant 0).
     *
     * @param targetTenantId The tenant purchasing the plan
     * @param planId         The plan being purchased
     */
    public Subscription subscribeTenant(Long targetTenantId, Integer planId) {
        verifyRootAccess("subscribeTenant"); // Must be executed as Root (or System Process)

        Plan plan = planRepo.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));

        // Check existing subscription
        Optional<Subscription> existing = subscriptionRepo.findByTargetTenantId(targetTenantId);
        Subscription subscription = existing.orElse(new Subscription());

        subscription.setPlan(plan);
        subscription.setTargetTenantId(targetTenantId);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(new Date());

        // Simple logic for next billing (30 days)
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, 30);
        subscription.setNextBillingDate(cal.getTime());

        // Since we are in Root Context, this saves to Tenant 0 tables (Platform Data)
        return subscriptionRepo.save(subscription);
    }

    /**
     * Record a financial transaction (Commission, Fee, etc.)
     * MUST be called within Root Context.
     */
    public void recordTransaction(Long sourceTenantId, double amount, BillingType type, String referenceId) {
        verifyRootAccess("recordTransaction");

        BillingTransaction tx = new BillingTransaction(amount, type, sourceTenantId, referenceId);
        transactionRepo.save(tx);

        LOGGER.info("💰 Revenue Recorded: Tenant={} Amount={} Type={} Ref={}",
                sourceTenantId, amount, type, referenceId);
    }

    /**
     * Get Subscription for a tenant.
     * Accessible by Root (for management) or the Tenant themselves (via read-only proxy if needed,
     * but this method specifically runs against the Repository which requires Root Context to see data).
     */
    public Optional<Subscription> getSubscriptionForTenant(Long targetTenantId) {
        verifyRootAccess("getSubscriptionForTenant");
        return subscriptionRepo.findByTargetTenantId(targetTenantId);
    }

    private void verifyRootAccess(String operation) {
        Long current = TenantContext.getTenantId();
        if (current == null || current != 0L) {
            throw new SecurityException("⛔ ACCESS DENIED: Operation '" + operation + "' requires Root Context (0). Current: " + current);
        }
    }
}
