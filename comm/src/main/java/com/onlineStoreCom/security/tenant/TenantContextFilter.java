package com.onlineStoreCom.security.tenant;

import com.onlineStoreCom.exception.TenantNotFoundException;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Strict TenantContextFilter.
 * Responsibilities:
 * 1. Resolve Tenant ID using TenantResolutionService.
 * 2. Set TenantContext.
 * 3. Fail closed if not capable of resolving.
 * <p>
 * NO Authorization.
 * NO Database Queries (delegated to Service).
 */
@Component
public class TenantContextFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantContextFilter.class);

    private final TenantResolutionService tenantResolutionService;

    @Autowired
    public TenantContextFilter(TenantResolutionService tenantResolutionService) {
        this.tenantResolutionService = tenantResolutionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        try {
            Long tenantId = null;

            // 1. Resolve from Subdomain/Host (Primary)
            String serverName = request.getServerName();
            String tenantKey = extractTenantKey(serverName);

            if (tenantKey != null) {
                tenantId = tenantResolutionService.resolveTenantId(tenantKey);
            } else if (isPlatformHost(serverName)) {
                // Handle Localhost/Platform access as Tenant 0
                tenantId = 0L;
            }

            // 2. Fallback: X-Tenant-ID Header (API/Testing override)
            if (tenantId == null) {
                String headerTenant = request.getHeader("X-Tenant-ID");
                if (headerTenant != null && !headerTenant.isEmpty()) {
                    try {
                        // First try to resolve as Key
                        tenantId = tenantResolutionService.resolveTenantId(headerTenant);
                    } catch (TenantNotFoundException e) {
                        try {
                            // Try as ID directly (use with caution)
                            tenantId = Long.parseLong(headerTenant);
                            LOGGER.debug("Resolved Tenant ID from Header: {}", tenantId);
                        } catch (NumberFormatException ex) {
                            LOGGER.warn("Invalid X-Tenant-ID header: {}", headerTenant);
                        }
                    }
                }
            }

            // 3. Fallback: Session (Post-Login context for Localhost/Platform access)
            if (tenantId == null || tenantId == 0L) {
                jakarta.servlet.http.HttpSession session = request.getSession(false);
                if (session != null) {
                    Long sessionTenantId = (Long) session.getAttribute("TENANT_ID");
                    if (sessionTenantId != null) {
                        tenantId = sessionTenantId; // Override 0 (Platform) with specific tenant if logged in
                        LOGGER.debug("Resolved Tenant ID from Session: {}", tenantId);
                    }
                }
            }

            // 3. Fail Closed
            if (tenantId == null) {
                LOGGER.error("No Tenant Context resolved for request: {}", request.getRequestURI());
                throw new TenantNotFoundException("No Tenant Context resolved.");
            }

            // 4. Set Context
            TenantContext.setTenantId(tenantId);

            // Note: We intentionally do NOT enable Hibernate Filters here.
            // Repository Layer (BaseTenantRepository) will enforce tenant_id limits
            // explicitly.

            chain.doFilter(request, response);

        } catch (TenantNotFoundException e) {
            LOGGER.warn("Tenant Resolution Blocked: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Tenant Not Found");
        } catch (Exception e) {
            LOGGER.error("Unexpected Error in TenantContextFilter", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Tenant Resolution Error");
        } finally {
            TenantContext.clear();
        }
    }

    private String extractTenantKey(String serverName) {
        if (serverName == null || isPlatformHost(serverName)) {
            return null;
        }

        // IP Address check
        if (serverName.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+$")) {
            return null;
        }

        String[] parts = serverName.split("\\.");
        // Ex: tenant1.saas.com -> tenant1
        if (parts.length > 0 && !parts[0].equalsIgnoreCase("www")) {
            return parts[0];
        }
        return null;
    }

    private boolean isPlatformHost(String serverName) {
        return serverName != null && (serverName.equalsIgnoreCase("localhost") || serverName.equals("127.0.0.1"));
    }
}
