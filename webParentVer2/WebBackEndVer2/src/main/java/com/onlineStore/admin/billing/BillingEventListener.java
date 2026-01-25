package com.onlineStore.admin.billing;

import com.onlineStore.admin.order.event.OrderCompletedEvent;
import com.onlineStoreCom.entity.billing.BillingType;
import com.onlineStoreCom.entity.billing.Plan;
import com.onlineStoreCom.entity.billing.Subscription;
import com.onlineStoreCom.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Component
public class BillingEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingEventListener.class);

    @Autowired
    private BillingService billingService;


    /**
     * Handle Order Completion to calculate Commission.
     * Runs AFTER the Order transaction is committed to ensure we don't bill for failed orders.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompletion(OrderCompletedEvent event) {
        Long tenantId = event.getTenantId();
        double orderTotal = event.getOrder().getTotal();
        String orderId = String.valueOf(event.getOrder().getId());

        LOGGER.info("🧾 Processing Verification for Tenant={} Order={} Amount={}", tenantId, orderId, orderTotal);

        // 1. Context Switch to ROOT (0) to access Billing Data
        // We must be careful to restore or ensure this runs in a new thread if Async, 
        // but TransactionalEventListener runs in standard sync (unless @Async added).
        // Safest is to explicitly switch permissions.

        Long previousContext = TenantContext.getTenantId();
        try {
            // SWITCH TO ROOT
            TenantContext.setTenantId(0L);

            // 2. Fetch Subscription to get Plan Commission Rate
            Optional<Subscription> subOpt = billingService.getSubscriptionForTenant(tenantId);

            if (subOpt.isPresent()) {
                Subscription sub = subOpt.get();
                Plan plan = sub.getPlan();

                if (plan.getCommissionRate() > 0) {
                    double commission = orderTotal * (plan.getCommissionRate() / 100.0);

                    // 3. Record Transaction
                    billingService.recordTransaction(
                            tenantId,
                            commission,
                            BillingType.PLATFORM_COMMISSION,
                            "ORDER-" + orderId
                    );
                }
            } else {
                LOGGER.warn("⚠️ No active subscription found for Tenant={}. Skipping commission.", tenantId);
            }

        } catch (Exception e) {
            LOGGER.error("❌ Failed to record commission for Tenant={} Order={}", tenantId, orderId, e);
        } finally {
            // RESTORE CONTEXT (Crucial if sharing thread)
            TenantContext.setTenantId(previousContext);
        }
    }
}
