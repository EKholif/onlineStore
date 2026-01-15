package com.onlineStoreCom.security.tenant;

import com.onlineStoreCom.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Unified TenantContextFilter for both FrontEnd and BackEnd.
 * Handles:
 * 1. JWT Cookie Extraction (ACCESS_TOKEN)
 * 2. Session Fallback (TENANT_ID)
 * 3. Header Fallback (X-Tenant-ID)
 * 4. Hibernate Filter Enablement
 */
@Component
public class TenantContextFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantContextFilter.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired(required = false)
    private TenantJwtHelper jwtHelper; // Interface for JWT extraction to decouple dependencies

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (isExcluded(path)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Long tenantId = null;
            String serverName = request.getServerName();
            if (serverName.equalsIgnoreCase("localhost")) {
                String hostHeader = request.getHeader("Host");
                if (hostHeader != null) {
                    serverName = hostHeader.split(":")[0];
                }
            }
            System.out.println("DEBUG: TenantContextFilter processing: " + path + ", ServerName: " + serverName);

            // 0. Try Query Parameter (Explicit Override for Testing)
            String paramTenant = request.getParameter("tenantId");
            if (paramTenant != null && !paramTenant.isEmpty()) {
                try {
                    tenantId = Long.parseLong(paramTenant);
                    LOGGER.debug("Found Tenant ID in Query Param: {}", tenantId);
                } catch (NumberFormatException e) {
                    LOGGER.warn("Invalid Tenant ID in Query Param: {}", paramTenant);
                }
            }

            // 1. Try Header (X-Tenant-ID) - Good for API/Postman
            String headerTenant = request.getHeader("X-Tenant-ID");
            if (tenantId == null && headerTenant != null && !headerTenant.isEmpty()) {
                try {
                    tenantId = Long.parseLong(headerTenant);
                    LOGGER.debug("Found Tenant ID in Header: {}", tenantId);
                } catch (NumberFormatException e) {
                    LOGGER.warn("Invalid Tenant ID in Header: {}", headerTenant);
                }
            }

            // 2. Try Subdomain (ServerName) [AG-TENANT-PROTOCOL] - High Priority
            if (tenantId == null) {
                // String serverName = request.getServerName(); // Removed duplicate
                String tenantKey = extractTenantKey(serverName);

                if (tenantKey != null) {
                    try {
                        System.out.println("DEBUG: Resolving tenantKey: " + tenantKey);
                        // Native Query used to decouple from Repository in Filter
                        Object result = entityManager.createNativeQuery("SELECT id FROM tenants WHERE code = :code")
                                .setParameter("code", tenantKey)
                                .getSingleResult();
                        System.out.println("DEBUG: Query Result: " + result);

                        if (result != null) {
                            tenantId = ((Number) result).longValue();
                            LOGGER.debug("Found Tenant ID in Subdomain: {} -> {}", tenantKey, tenantId);
                        }
                    } catch (jakarta.persistence.NoResultException e) {
                        System.err.println("DEBUG: Tenant Not Found (NoResult) for key: " + tenantKey);
                        LOGGER.warn("Tenant not found for key: {}. Native Query failed.", tenantKey);
                        // [AG-TEN-SEC-008] Handle Invalid Tenant Logic
                        handleInvalidTenant(request, response);
                        return; // Stop filter chain
                    } catch (Exception e) {
                        System.err.println("DEBUG: Exception resolving tenant: " + e.getMessage());
                        e.printStackTrace();
                        LOGGER.error("Error resolving tenant from subdomain: {}. Exception: {}", tenantKey,
                                e.getMessage(), e);
                    }
                }
            }

            // 3. Try JWT Cookie (ACCESS_TOKEN)
            if (tenantId == null && jwtHelper != null) {
                tenantId = jwtHelper.extractTenantIdFromRequest(request);
                if (tenantId != null) {
                    LOGGER.debug("Found Tenant ID in JWT: {}", tenantId);
                }
            }

            // 4. Try Session (Fallback)
            if (tenantId == null) {
                Object sessionVal = request.getSession().getAttribute("TENANT_ID");
                if (sessionVal instanceof Long) {
                    tenantId = (Long) sessionVal;
                    LOGGER.debug("Found Tenant ID in Session: {}", tenantId);
                }
            }

            // 4. Set Context & Enable Filter
            if (tenantId != null) {
                TenantContext.setTenantId(tenantId);
                enableHibernateFilter(tenantId);

                // [AG-TEN-SEC-007] Persist to Session so subsequent requests (CSS/Images) know
                // the tenant
                if (request.getSession().getAttribute("TENANT_ID") == null ||
                        !request.getSession().getAttribute("TENANT_ID").equals(tenantId)) {
                    request.getSession().setAttribute("TENANT_ID", tenantId);
                    LOGGER.debug("Persisted Tenant ID {} to Session.", tenantId);
                }
            } else {
                LOGGER.debug("No Tenant ID found for request: {}. Defaulting to System (0).", path);
                // [AG-TEN-SEC-005] FAIL SAFE: Always enable the filter.
                TenantContext.setTenantId(0L);
                enableHibernateFilter(0L);
            }

            if (tenantId != null) {
                response.addHeader("X-Tenant-ID", String.valueOf(tenantId));
            }
            chain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }

    private void handleInvalidTenant(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // [AG-TEN-SEC-008] STRICT ISOLATION RECOVERY
        // If tenant is invalid or missing, redirect to central landing page
        // Hardcoded for now, potential to move to properties
        String landingPage = "http://www.localhost:720"; // Or external landing

        // Avoid infinite redirect loop if we are already on the landing page
        String serverName = request.getServerName();
        if (serverName.equalsIgnoreCase("www.localhost") || serverName.equalsIgnoreCase("localhost")) {
            // We are on landing, just proceed (Tenant ID 0 will be set later)
            return;
        }

        LOGGER.warn("Invalid Tenant for host: {}. Redirecting to Landing Page: {}", serverName, landingPage);
        response.sendRedirect(landingPage);
    }

    private void enableHibernateFilter(Long tenantId) {
        try {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
        } catch (Exception e) {
            LOGGER.error("Failed to enable Hibernate filter for tenant: {}", tenantId, e);
        }
    }

    private boolean isExcluded(String path) {
        // [AG-TEN-SEC-004] Do NOT exclude static resources.
        // We need TenantContext for CSS (Theme) and Images (Logo).
        // Only exclude explicit login endpoint avoiding circular checks?
        return path.equals("/login");
    }

    private String extractTenantKey(String serverName) {
        if (serverName == null || serverName.equalsIgnoreCase("localhost"))
            return null;

        // Guard: IP address (simple check)
        if (serverName.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+$"))
            return null;

        String[] parts = serverName.split("\\.");

        // Guard: www
        if (parts[0].equalsIgnoreCase("www"))
            return null;

        // Return first part as tenant key
        if (parts.length > 0) {
            return parts[0];
        }

        return null;
    }
}
