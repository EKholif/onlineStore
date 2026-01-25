package com.onlineStore.admin;

import com.onlineStore.admin.usersAndCustomers.users.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Main controller for handling root and authentication-related requests.
 * <p>
 * This controller manages the main entry points of the application including
 * the home page and login page. It handles redirections and authentication
 * checks.
 * 
 * @Controller - Marks this class as a Spring MVC controller
 */
@Controller
public class MainController {

    @Autowired
    private RoleRepository repo;

    /**
     * Handles requests to the root URL (/) and redirects to the users page.
     * 
     * @return String representing the redirect path to "/users/users"
     */
    @GetMapping("/")
    public String ViewHome() {
        return "redirect:/users/users";
    }

    /**
     * Handles requests to the "/ind" URL and returns the index page.
     * 
     * @return String representing the view name "index"
     */
    @GetMapping("/ind")
    public String View() {
        return "/index";
    }

    /**
     * Handles requests to the login page.
     * <p>
     * If the user is already authenticated, redirects to the home page.
     * Otherwise, displays the login page.
     * 
     * @return String representing the view name "login" or a redirect to home
     */
    @GetMapping("/login")
    public String viewLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("MainController.viewLoginPage called. Auth: " + authentication);
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            System.out.println("MainController: returning login view");
            return "login";
        }
        System.out.println("MainController: redirecting to /");
        return "redirect:/";
    }

    /**
     * Universal Dashboard Entry Point.
     * Routes to:
     * - Root: Marketplace Overview (admin/marketplace_dashboard)
     * - Tenant: Store Statistics (tenant/store_dashboard)
     */
    @Autowired
    private com.onlineStore.services.service.analytics.AnalyticsService analyticsService;
    @Autowired
    private com.onlineStore.services.service.repository.ProductRepository productRepo;

    /**
     * Platform Home Redirect.
     * Serves as the safe landing page for Root Admin.
     */
    @GetMapping("/platform/home")
    public String viewPlatformHome() {
        return "redirect:/dashboard";
    }

    /**
     * Universal Dashboard Entry Point.
     * Routes to:
     * - Root: Marketplace Overview (admin/marketplace_dashboard)
     * - Tenant: Store Statistics (tenant/store_dashboard)
     */
    @GetMapping("/dashboard")
    public String viewDashboard(org.springframework.ui.Model model) {
        Long tenantIdLong = com.onlineStoreCom.tenant.TenantContext.getTenantId();

        if (tenantIdLong != null && tenantIdLong == 0L) {
            model.addAttribute("pageTitle", "Platform Marketplace");

            // Platform Analytics
            Double platformRevenue = analyticsService.getPlatformTotalRevenue(new java.util.Date());
            model.addAttribute("platformRevenue", platformRevenue);

            long activeTenants = analyticsService.getPlatformActiveTenantsCount();
            model.addAttribute("activeTenants", activeTenants);

            java.util.List<com.onlineStoreCom.entity.analytics.SearchKeyword> platformKeywords = analyticsService
                    .getPlatformTopSearchKeywords(5);
            model.addAttribute("platformKeywords", platformKeywords);

            return "admin/marketplace_dashboard";
        } else {
            model.addAttribute("pageTitle", "My Store Dashboard");

            if (tenantIdLong != null) {
                Integer tenantId = tenantIdLong.intValue();

                // Analytics - Top Viewed
                java.util.List<Object[]> topViewed = analyticsService.getTopViewedProducts(tenantId, 5);
                java.util.List<DashboardProductStats> topViewedList = new java.util.ArrayList<>();
                for (Object[] row : topViewed) {
                    Integer pid = (Integer) row[0];
                    Long count = (Long) row[1];
                    String name = productRepo.findById(pid).map(p -> p.getName())
                            .orElse("Unknown Product (" + pid + ")");
                    topViewedList.add(new DashboardProductStats(name, count));
                }
                model.addAttribute("topViewed", topViewedList);

                // Analytics - Top Selling
                java.util.List<Object[]> topSelling = analyticsService.getTopSellingProducts(tenantId, 5);
                java.util.List<DashboardProductStats> topSellingList = new java.util.ArrayList<>();
                for (Object[] row : topSelling) {
                    Integer pid = (Integer) row[0];
                    Long count = (Long) row[1]; // Sales Count
                    // row[2] is revenue
                    String name = productRepo.findById(pid).map(p -> p.getName())
                            .orElse("Unknown Product (" + pid + ")");
                    topSellingList.add(new DashboardProductStats(name, count));
                }
                model.addAttribute("topSelling", topSellingList);

                // Analytics - Top Keywords
                java.util.List<com.onlineStoreCom.entity.analytics.SearchKeyword> topKeywords = analyticsService
                        .getTopSearchKeywords(tenantId, 5);
                model.addAttribute("topKeywords", topKeywords);

                // Analytics - Daily Revenue
                Double dailyRevenue = analyticsService.getDailyRevenue(tenantId, new java.util.Date());
                model.addAttribute("dailyRevenue", dailyRevenue);
            }

            return "tenant/store_dashboard";
        }
    }

    // Simple DTO for Dashboard Display
    public static class DashboardProductStats {
        private String name;
        private Long value;

        public DashboardProductStats(String name, Long value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Long getValue() {
            return value;
        }
    }
}
