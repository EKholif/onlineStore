package com.onlineStore.admin.billing;

import com.onlineStore.admin.audit.AuditLogService;
import com.onlineStoreCom.entity.billing.BillingTransaction;
import com.onlineStoreCom.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/billing")
public class BillingRestController {

    @Autowired
    private BillingTransactionRepository transactionRepo;

    @Autowired
    private AuditLogService auditLogService;

    /**
     * Get list of transactions.
     * ROOT: All Transactions.
     * TENANT: Only Own Transactions.
     */
    @GetMapping("/transactions")
    public List<BillingTransaction> getTransactions() {
        Long currentTenant = TenantContext.getTenantId();
        List<BillingTransaction> list;

        if (currentTenant == 0L) {
            list = transactionRepo.findAll();
            auditLogService.log("BILLING", "VIEW_LEDGER", "Root viewed all transactions");
        } else {
            list = transactionRepo.findBySourceTenantId(currentTenant);
            auditLogService.log("BILLING", "VIEW_PAYMENTS", "Tenant viewed payment history");
        }
        return list;
    }

    /**
     * Get Report Data (JSON for Charts).
     * Returns: {
     * "totalRevenue": 12345.00,
     * "revenueByMonth": { "Jan": 100, "Feb": 200 },
     * "commissionByTenant": { "Tenant 5": 500 } (Root Only)
     * }
     */
    @GetMapping("/reports")
    public Map<String, Object> getReports() {
        Long currentTenant = TenantContext.getTenantId();
        Map<String, Object> response = new HashMap<>();

        List<BillingTransaction> list;
        if (currentTenant == 0L) {
            list = transactionRepo.findAll();
            auditLogService.log("BILLING", "VIEW_REPORTS", "Root viewed revenue reports");
        } else {
            list = transactionRepo.findBySourceTenantId(currentTenant);
            auditLogService.log("BILLING", "VIEW_REPORTS", "Tenant viewed revenue reports");
        }

        // 1. Total Revenue
        double totalRevenue = list.stream().mapToDouble(BillingTransaction::getAmount).sum();
        response.put("totalRevenue", totalRevenue);
        response.put("transactionCount", list.size());

        // 2. Revenue By Tenant (Root Only) - Simplified Aggregation
        if (currentTenant == 0L) {
            Map<Long, Double> byTenant = list.stream()
                    .collect(Collectors.groupingBy(
                            BillingTransaction::getSourceTenantId,
                            Collectors.summingDouble(BillingTransaction::getAmount)
                    ));
            response.put("revenueByTenant", byTenant);
        }

        // 3. Simple Timeline (Just count by Type for now to demo)
        // In a real app we'd group by Date (Month/Week)
        Map<String, Long> byType = list.stream()
                .collect(Collectors.groupingBy(
                        tx -> tx.getType().name(),
                        Collectors.counting()
                ));
        response.put("transactionsByType", byType);

        return response;
    }
}
