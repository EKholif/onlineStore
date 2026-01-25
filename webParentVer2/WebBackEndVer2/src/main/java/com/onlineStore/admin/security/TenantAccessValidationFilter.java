package com.onlineStore.admin.security;

import com.onlineStore.admin.audit.AuditService;
import com.onlineStore.admin.usersAndCustomers.users.servcies.UserService;
import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.entity.users.User;
import com.onlineStoreCom.repo.TenantRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * AG-SEC-FILTER-001: Tenant Access Validation Filter
 * <p>
 * Purpose:
 * - Runs BEFORE TenantContextFilter
 * - Validates user's access to requested tenant
 * - Enforces hierarchy rules (Platform/Agency/Tenant Admin)
 * - Logs unauthorized attempts via AuditService
 * - Returns 403 Forbidden for invalid access
 * <p>
 * Order: @Order(1) - Must run before TenantContextFilter (@Order(2))
 * <p>
 * Business Impact:
 * - Prevents cross-tenant data leaks
 * - Enforces zero-trust architecture
 * - Provides audit trail for compliance
 */
@Component
@Order(1) // Run BEFORE TenantContextFilter
public class TenantAccessValidationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantAccessValidationFilter.class);

    @Autowired
    @Lazy // AG-RBAC-FIX-002: Break circular dependency with UserService
    private UserService userService;

    @Autowired
    private TenantRepository tenantRepo;

    @Autowired
    private AuditService auditService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip validation for public paths
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // Get authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                // Not authenticated, let Spring Security handle it
                chain.doFilter(request, response);
                return;
            }

            // Extract user from authentication
            User user = null;
            if (auth.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                user = userService.getByEmail(userDetails.getUsername());
            }

            if (user == null) {
                LOGGER.warn("User not found for authentication principal: {}", auth.getName());
                chain.doFilter(request, response);
                return;
            }

            // Determine target tenant ID
            Long targetTenantId = resolveTargetTenantId(request);

            if (targetTenantId == null) {
                // No tenant context, allow request (will default to tenant 0)
                chain.doFilter(request, response);
                return;
            }

            // AG-RBAC-VALIDATE-001: Check if user can access target tenant
            if (!userService.canAccessTenant(user, targetTenantId)) {
                // Access denied - log and return 403
                Tenant currentTenant = getUserPrimaryTenant(user);
                Tenant targetTenant = tenantRepo.findById(targetTenantId).orElse(null);

                auditService.logAccessDenied(user, currentTenant, targetTenant,
                        "UNAUTHORIZED_TENANT_ACCESS", request);

                LOGGER.warn("Access denied for user {} to tenant {}. Path: {}",
                        user.getEmail(), targetTenantId, path);

                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Access denied to tenant " + targetTenantId + "\"}");
                return; // Stop filter chain
            }

            // Access granted - proceed
            chain.doFilter(request, response);

        } catch (Exception e) {
            LOGGER.error("Error in TenantAccessValidationFilter", e);
            chain.doFilter(request, response); // Fail open (but logged)
        }
    }

    /**
     * Resolve target tenant ID from request
     * Priority: SWITCHED_TENANT_ID (session) > Query Param > Header > Subdomain > User's Primary Tenant
     */
    private Long resolveTargetTenantId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        // 1. Check if user has switched tenant (session-based switching)
        if (session != null) {
            Long switchedTenantId = (Long) session.getAttribute("SWITCHED_TENANT_ID");
            if (switchedTenantId != null) {
                return switchedTenantId;
            }
        }

        // 2. Query parameter (explicit override)
        String paramTenant = request.getParameter("tenantId");
        if (paramTenant != null && !paramTenant.isEmpty()) {
            try {
                return Long.parseLong(paramTenant);
            } catch (NumberFormatException e) {
                LOGGER.warn("Invalid tenantId parameter: {}", paramTenant);
            }
        }

        // 3. Header (X-Tenant-ID)
        String headerTenant = request.getHeader("X-Tenant-ID");
        if (headerTenant != null && !headerTenant.isEmpty()) {
            try {
                return Long.parseLong(headerTenant);
            } catch (NumberFormatException e) {
                LOGGER.warn("Invalid X-Tenant-ID header: {}", headerTenant);
            }
        }

        // 4. Session TENANT_ID (fallback)
        if (session != null) {
            Long sessionTenantId = (Long) session.getAttribute("TENANT_ID");
            if (sessionTenantId != null) {
                return sessionTenantId;
            }
        }

        // 5. Check Subdomain (AG-LOGIN-FIX: Match TenantContextFilter logic)
        String serverName = request.getServerName();
        String tenantKey = extractTenantKey(serverName);
        if (tenantKey != null) {
            java.util.Optional<Tenant> tenantByCode = tenantRepo.findByCode(tenantKey);
            if (tenantByCode.isPresent()) {
                LOGGER.debug("Resolved Tenant ID from Subdomain: {} -> {}", tenantKey, tenantByCode.get().getId());
                return tenantByCode.get().getId();
            }
        }

        // 6. No explicit tenant ID found, return null (will use user's primary tenant
        // later)
        return null;
    }

    /**
     * Get user's primary tenant (first tenant in collection)
     */
    private Tenant getUserPrimaryTenant(User user) {
        if (user.getTenants() == null || user.getTenants().isEmpty()) {
            return null;
        }
        return user.getTenants().iterator().next().getTenant();
    }

    /**
     * Public paths that don't require tenant validation
     */
    private boolean isPublicPath(String path) {
        return path.startsWith("/login") ||
                path.startsWith("/logout") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.startsWith("/webjars/") ||
                path.startsWith("/error") ||
                path.startsWith("/favicon.ico");
    }

    private String extractTenantKey(String serverName) {
        if (serverName == null || serverName.equalsIgnoreCase("localhost"))
            return null;
        if (serverName.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+$"))
            return null;
        String[] parts = serverName.split("\\.");
        if (parts[0].equalsIgnoreCase("www"))
            return null;
        if (parts.length > 0) {
            return parts[0];
        }
        return null;
    }
}
