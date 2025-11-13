package com.onlineStore.admin.security.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantIdFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String SESSION_TENANT_ID_KEY = "TENANT_ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Long currentTenantId = null;
            HttpSession session = request.getSession(false);

            // 1. Try to get Long Tenant ID from the Session (for authenticated users)
            if (session != null && session.getAttribute(SESSION_TENANT_ID_KEY) != null) {
                currentTenantId = (Long) session.getAttribute(SESSION_TENANT_ID_KEY);
            }

            // 2. If not in session, check the Header (typically for the initial login POST)
            if (currentTenantId == null) {
                String tenantIdStr = request.getHeader(TENANT_HEADER);
                if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
                    try {
                        currentTenantId = Long.parseLong(tenantIdStr);
                    } catch (NumberFormatException e) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                "Invalid Tenant ID format in " + TENANT_HEADER + " header. Must be a Long.");
                        return;
                    }
                }
            }

            // 3. Set the context if an ID was found (otherwise, TenantContext uses the MASTER_TENANT_ID)
            if (currentTenantId != null) {
                TenantContext.setTenantId(currentTenantId);
            }

            filterChain.doFilter(request, response);

        } finally {
            // CRITICAL: Clear the context
            TenantContext.clear();
        }
    }
}