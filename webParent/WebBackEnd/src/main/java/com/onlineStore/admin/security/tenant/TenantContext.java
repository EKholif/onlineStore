package com.onlineStore.admin.security.tenant;

import com.onlineStore.admin.security.StoreUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class TenantContext {

    private static final ThreadLocal<Long> currentTenant = new ThreadLocal<>();

    public static void setTenantId(Long tenantId) {
        currentTenant.set(tenantId);
    }

    public static Long getTenantId() {
        // 1. ThreadLocal
        Long tenantId = currentTenant.get();
        if (tenantId != null) return tenantId;

        // 2. Session fallback
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            HttpSession session = request.getSession(false);
            if (session != null) {
                tenantId = (Long) session.getAttribute("TENANT_ID");
                if (tenantId != null) {
                    currentTenant.set(tenantId);
                    return tenantId;
                }
            }
        }

        // 3. SecurityContext fallback
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof StoreUserDetails) {
            tenantId = ((StoreUserDetails) auth.getPrincipal()).getTenantId();
            currentTenant.set(tenantId);
            return tenantId;
        }

        // 4. قبل login أو fallback
        return 0L;
    }

    public static void clear() {
        currentTenant.remove();
    }
}
