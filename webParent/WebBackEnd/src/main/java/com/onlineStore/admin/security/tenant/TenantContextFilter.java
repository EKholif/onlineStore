package com.onlineStore.admin.security.tenant;

import com.onlineStore.admin.security.StoreUserDetails;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantContextFilter extends OncePerRequestFilter {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        // exclude login and static resources
        if (path.equals("/login") || path.startsWith("/css") || path.startsWith("/js")
                || path.startsWith("/images") || path.startsWith("/webjars")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Long tenantId = TenantContext.getTenantId();

            // 1. Try to get from Session (Fastest)
            if (tenantId == null) {
                Object val = request.getSession().getAttribute("TENANT_ID");
                if (val instanceof Long) {
                    tenantId = (Long) val;
                    TenantContext.setTenantId(tenantId);
                }
            }

            // 2. [AG-TEN-BUG-001] Fallback: Get from SecurityContext
            if (tenantId == null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getPrincipal() instanceof StoreUserDetails) {
                    StoreUserDetails userDetails = (StoreUserDetails) authentication.getPrincipal();
                    tenantId = userDetails.getTenantId();
                    TenantContext.setTenantId(tenantId);
                    request.getSession().setAttribute("TENANT_ID", tenantId); // Self-heal
                }
            }

            // [AG-ARCH-SEC-002] Fail-Fast: Block request if TenantID is missing
            if (tenantId == null || tenantId == 0) {
                System.err.println("⛔ [TenantContextFilter] Access Denied: No TenantID found for " + path);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Tenant Context Missing");
                return;
            }

            // [AG-ARCH-OBS-001] MDC Logging
            org.slf4j.MDC.put("tenantId", String.valueOf(tenantId));

            // 3. Apply Hibernate Filter
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
            System.out.println("✅ [TenantContextFilter] Enabled Filter for TenantID: " + tenantId);

            chain.doFilter(request, response);

        } finally {
            // clear ThreadLocal & MDC to avoid leak
            TenantContext.clear();
            org.slf4j.MDC.clear();
        }
    }
}
