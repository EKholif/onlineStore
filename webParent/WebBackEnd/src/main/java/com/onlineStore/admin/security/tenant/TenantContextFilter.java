package com.onlineStore.admin.security.tenant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
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
        Session session = entityManager.unwrap(Session.class);

        if (tenantId != null && tenantId != 0L) {
            session.enableFilter("tenantFilter")
                    .setParameter("tenantId", tenantId);
//            logger.debug("Tenant filter enabled for tenantId={}", tenantId);
        } else {
            session.disableFilter("tenantFilter");
            logger.debug("Tenant filter disabled (tenantId missing or zero)");
        }

        try {
            chain.doFilter(request, response);
        } finally {
            // clear ThreadLocal to avoid leak between requests
            TenantContext.clear();
        }
    }
}
