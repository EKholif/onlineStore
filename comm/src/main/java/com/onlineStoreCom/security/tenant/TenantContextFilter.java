package com.onlineStoreCom.security.tenant;

import com.onlineStoreCom.tenant.TenantContext;
import com.onlineStoreCom.tenant.TenantDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

            // 2. Try JWT Cookie (ACCESS_TOKEN)
            if (tenantId == null && jwtHelper != null) {
                tenantId = jwtHelper.extractTenantIdFromRequest(request);
                if (tenantId != null) {
                    LOGGER.debug("Found Tenant ID in JWT: {}", tenantId);
                }
            }

            // 3. Try Session (Fallback)
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

            chain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
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
}
