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

        Long tenantId = TenantContext.getTenantId();

        // 1. Try to get from Session (Fastest)
        if (tenantId == null) {
            Object val = request.getSession().getAttribute("TENANT_ID");
            if (val instanceof Long) {
                tenantId = (Long) val;
                TenantContext.setTenantId(tenantId);
            }
        }

        // 2. [AG-TEN-BUG-001] Fallback: Get from SecurityContext (Robust for RememberMe
        // / Session Timeout)
        if (tenantId == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof StoreUserDetails) {
                StoreUserDetails userDetails = (StoreUserDetails) authentication.getPrincipal();
                tenantId = userDetails.getTenantId();

                // Self-Heal: Put back in Session and Context
                TenantContext.setTenantId(tenantId);
                request.getSession().setAttribute("TENANT_ID", tenantId);
            }
        }

        // 3. Apply Hibernate Filter
        Session session = entityManager.unwrap(Session.class);
        if (tenantId != null && tenantId != 0) {
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
        } else {
            session.disableFilter("tenantFilter");
        }

        try {
            chain.doFilter(request, response);
        } finally {
            // clear ThreadLocal to avoid leak between requests
            TenantContext.clear();
        }
    }
}
