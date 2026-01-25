package com.onlineStore.admin.dashboard;

import com.onlineStore.admin.billing.BillingTransactionRepository;
import com.onlineStore.admin.order.OrderRepository;
import com.onlineStore.admin.tenant.TenantService;
import com.onlineStoreCom.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRestController {

    @Autowired
    private BillingTransactionRepository billingTransactionRepo;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private OrderRepository orderRepo;

    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        Long tenantId = TenantContext.getTenantId();
        boolean isRoot = (tenantId != null && tenantId == 0L);

        if (isRoot) {
            // Root View: Platform Metrics
            Double totalCommissions = billingTransactionRepo.sumTotalAmount();
            int totalTenants = tenantService.listAll().size();

            stats.put("type", "ROOT");
            stats.put("revenue", totalCommissions != null ? totalCommissions : 0.0);
            stats.put("tenantCount", totalTenants);

            // For Root, 'Orders' might mean Subscription Orders or just zero if not selling products
            // keeping it simple for now.
        } else {
            // Tenant View: Store Metrics
            Double totalSales = orderRepo.sumTotalSales();
            Long totalOrders = orderRepo.countTotalOrders();

            stats.put("type", "TENANT");
            stats.put("revenue", totalSales != null ? totalSales : 0.0);
            stats.put("orderCount", totalOrders != null ? totalOrders : 0);
        }

        return stats;
    }
}
