package com.onlineStore.admin.billing;


import com.onlineStore.admin.order.event.OrderCompletedEvent;
import com.onlineStoreCom.entity.billing.BillingType;
import com.onlineStoreCom.entity.billing.Plan;
import com.onlineStoreCom.entity.billing.Subscription;
import com.onlineStoreCom.entity.order.Order;
import com.onlineStoreCom.entity.order.OrderStatus;
import com.onlineStoreCom.tenant.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Billing & Revenue Isolation Tests")
public class BillingIsolationTest {

    @Autowired
    private BillingEventListener billingEventListener;

    @Autowired
    private BillingService billingService;

    @Autowired
    private PlanRepository planRepo;

    @Autowired
    private SubscriptionRepository subscriptionRepo;

    @Autowired
    private BillingTransactionRepository transactionRepo;

    @MockBean
    private ApplicationEventPublisher eventPublisher;

    private Plan standardPlan;
    private Subscription activeSubscription;
    private Long tenantId = 5L;

    @BeforeEach
    void setUp() {
        // 1. Create Plan (10% commission)
        TenantContext.setTenantId(0L); // As Root
        standardPlan = new Plan("Standard Store", 29.99, "MONTHLY");
        standardPlan.setCommissionRate(10.0);
        planRepo.save(standardPlan);

        // 2. Create Subscription
        activeSubscription = new Subscription();
        activeSubscription.setPlan(standardPlan);
        activeSubscription.setTargetTenantId(tenantId);
        activeSubscription.setStartDate(new Date());
        subscriptionRepo.save(activeSubscription);

        TenantContext.clear();
    }

    @Test
    @DisplayName("Root Context (0) SHOULD see Billing Data")
    void rootCanSeeBillingData() {
        TenantContext.setTenantId(0L);
        Optional<Subscription> sub = billingService.getSubscriptionForTenant(tenantId);
        assertTrue(sub.isPresent(), "Root should see subscription");
    }

    @Test
    @DisplayName("Tenant Context (5) SHOULD NOT see Billing Service directly (SecurityException)")
    void tenantCannotAccessBillingService() {
        TenantContext.setTenantId(tenantId); // As Tenant 5

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            billingService.getSubscriptionForTenant(tenantId);
        });

        assertTrue(exception.getMessage().contains("ACCESS DENIED"), "Should have access denied message");
    }

    @Test
    @DisplayName("Billing Event Listener SHOULD record commission on Order Completion")
    void testRevenueEventProcessing() {
        // Simulate Order Completion in Tenant Context
        TenantContext.setTenantId(tenantId);

        Order mockOrder = new Order();
        mockOrder.setId(101);
        mockOrder.setTotal(200.0f); // $200 Order
        mockOrder.setStatus(OrderStatus.DELIVERED);

        OrderCompletedEvent event = new OrderCompletedEvent(this, mockOrder, tenantId);

        // TRIGGER LISTENER manually (since we are testing the listener logic, not the publisher)
        billingEventListener.handleOrderCompletion(event);

        // VERIFY: Switch to Root to check Ledger
        TenantContext.setTenantId(0L);
        var transactions = transactionRepo.findBySourceTenantId(tenantId);

        assertFalse(transactions.isEmpty(), "Should have recorded a transaction");
        assertEquals(1, transactions.size());

        var tx = transactions.get(0);
        assertEquals(20.0, tx.getAmount(), 0.01, "Should be 10% of 200");
        assertEquals(BillingType.PLATFORM_COMMISSION, tx.getType());
        assertEquals("ORDER-101", tx.getReferenceId());
    }
}
