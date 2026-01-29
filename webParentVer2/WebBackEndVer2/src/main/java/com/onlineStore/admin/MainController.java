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
     * Universal Dashboard Entry Point.
     * Routes to:
     * - Root: Marketplace Overview (admin/marketplace_dashboard)
     * - Tenant: Store Statistics (tenant/store_dashboard)
     */
    @Autowired
    private com.onlineStoreCom.analytics.ProductAnalyticsService analyticsService;

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
     * Handles requests to the root URL (/) and redirects to the users page.
     *
     * @return String representing the redirect path to "/users/users"
     */
    @GetMapping("/")
    public String ViewHome() {
        return "redirect:/dashboard";
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
        System.out.println("MainController: redirecting to /dashboard");
        return "redirect:/dashboard";
    }
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
            model.addAttribute("platformRevenue", platformRevenue != null ? platformRevenue : 0.0);

            Long activeTenants = analyticsService.getActiveTenantsCount();
            model.addAttribute("activeTenants", activeTenants);

            // Global Top Viewed
            java.util.List<Object[]> globalTop = analyticsService.getGlobalTopViewedProducts(5);
            java.util.List<DashboardProductStats> globalTopList = new java.util.ArrayList<>();
            for (Object[] row : globalTop) {
                // row: [productId, totalViews]
                // We need to fetch product name relative to the tenant?
                // Global products view usually involves products that might be from any tenant.
                // Resolving name globally might be tricky if "products" table has "tenant_id".
                // But ProductRepository filters by tenant.
                // "Global Top Viewed" is difficult if we can't fetch names.
                // The query `getGlobalTopViewedProducts` returns `product_id`.
                // If we use productRepo.findById(pid), it will filter by Current Tenant (0).
                // Tenant 0 can see all products if Filter is correct (Tenant 0 OR tenant_id=0).
                // Product Filter: `tenant_id = :tenantId OR tenant_id = 0`.
                // If we are Tenant 0, :tenantId is 0. So `tenant_id = 0 OR tenant_id = 0`.
                // Wait. The filter says `tenant_id = :tenantId OR tenant_id = 0`.
                // If I am Tenant 0, I only see products with tenant_id = 0?
                // That means I DON'T see Tenant 1's products.
                // For "Global Top Viewed", I need to see Tenant 1's products too.
                // This implies "Global Top Viewed" might need the Name in the query itself to
                // be safe.
                // Currently `getGlobalTopViewedProducts` returns `product_id, total_views`.
                // I should update `getGlobalTopViewedProducts` in Repository to return names
                // too via Native Query join.
                // "SELECT p.name, SUM(s.view_count) ... JOIN products p ..."
                // But I've already written Repos and Service.
                // I'll update the Controller to TRY to fetch name. If "Unknown", so be it.
                // Or, I can do a quick update to Repo to fetch name if I am careful.
                // Let's stick to `productRepo.findById` and see.
                Integer pid = (Integer) row[0];
                Long count = ((Number) row[1]).longValue();
                String name = productRepo.findById(pid).map(p -> p.getName()).orElse("Product #" + pid);
                globalTopList.add(new DashboardProductStats(name, count));
            }
            model.addAttribute("globalTopViewed", globalTopList);

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
                    Long count = ((Number) row[1]).longValue();
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
                    Long count = ((Number) row[1]).longValue();
                    String name = productRepo.findById(pid).map(p -> p.getName())
                            .orElse("Unknown Product (" + pid + ")");
                    topSellingList.add(new DashboardProductStats(name, count));
                }
                model.addAttribute("topSelling", topSellingList);

                // Analytics - Zero Views (Optimization)
                org.springframework.data.domain.Page<Object[]> zeroViewPage = analyticsService
                        .getZeroViewProducts(tenantId, 0, 5);
                java.util.List<DashboardProductStats> zeroViewList = new java.util.ArrayList<>();
                for (Object[] row : zeroViewPage.getContent()) {
                    // Query: SELECT p.id, p.name ...
                    Integer pid = (Integer) row[0];
                    String name = (String) row[1];
                    zeroViewList.add(new DashboardProductStats(name, 0L));
                }
                model.addAttribute("zeroViewProducts", zeroViewList);
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
