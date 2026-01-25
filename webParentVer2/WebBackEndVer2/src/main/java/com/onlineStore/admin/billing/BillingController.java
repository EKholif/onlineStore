package com.onlineStore.admin.billing;

import com.onlineStoreCom.entity.billing.BillingTransaction;
import com.onlineStoreCom.entity.billing.Plan;
import com.onlineStoreCom.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @Autowired
    private BillingTransactionRepository transactionRepo;

    @Autowired
    private PlanRepository planRepo;

    @Autowired
    private SubscriptionRepository subRepo;

    /**
     * Root Dashboard: Shows Revenue, Plans, and Transactions.
     * Restricted to Tenant 0.
     */
    @GetMapping("/dashboard")
    public String viewDashboard(Model model) {
        if (!isRoot()) {
            return "redirect:/platform/home";
        }

        // 1. Fetch Stats (Simple counts for now)
        long totalTransactions = transactionRepo.count();
        List<Plan> plans = planRepo.findAll();

        model.addAttribute("totalTransactions", totalTransactions);
        model.addAttribute("plans", plans);
        model.addAttribute("pageTitle", "Billing Dashboard");

        return "billing/dashboard";
    }

    /**
     * Transaction History (Ledger).
     * Root sees ALL. Tenants see OWN.
     */
    @GetMapping("/transactions")
    public String viewTransactions(Model model) {
        Long currentTenant = TenantContext.getTenantId();

        List<BillingTransaction> list;
        if (currentTenant == 0L) {
            // Root sees all
            list = transactionRepo.findAll();
        } else {
            // Tenant sees own
            list = transactionRepo.findBySourceTenantId(currentTenant);
        }

        model.addAttribute("transactions", list);
        model.addAttribute("pageTitle", "Billing Transactions");

        return "billing/transactions";
    }

    /**
     * Tenant View: My Plan & Subscription.
     */
    @GetMapping("/my-plan")
    public String viewMyPlan(Model model) {
        Long currentTenant = TenantContext.getTenantId();

        // Security: Root doesn't have a plan (they are the platform)
        if (currentTenant == 0L) {
            return "redirect:/billing/dashboard";
        }

        // We fetch transparently.
        model.addAttribute("pageTitle", "My Subscription");
        model.addAttribute("message", "Subscription management coming soon (Requires secure context bridge).");

        return "billing/my_plan";
    }

    private boolean isRoot() {
        Long current = TenantContext.getTenantId();
        return current != null && current == 0L;
    }
}
