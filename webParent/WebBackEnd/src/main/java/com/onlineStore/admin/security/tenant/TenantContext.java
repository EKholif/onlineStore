package com.onlineStore.admin.security.tenant;

import com.onlineStore.admin.security.StoreUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class TenantContext {

    private static final ThreadLocal<Long> currentTenant = new ThreadLocal<>();
    private static final Long MASTER_TENANT_ID = 0L;

    public static void setTenantId(Long tenantId) {
        currentTenant.set(tenantId);
    }

    public static Long getTenantId() {
        // أولاً: نحاول نجيب الـ tenantId من الـ ThreadLocal
        Long tenantId = currentTenant.get();
        if (tenantId != null) {
            return tenantId;
        }

        // ثانياً: لو ThreadLocal فاضي، نحاول نجيب من Spring Security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof StoreUserDetails) {
            // نفترض إن عندك CustomUserDetails يحتوي على tenantId
            return ((StoreUserDetails) auth.getPrincipal()).getTenantId();
        }

        // أخيراً: لو مفيش حاجة، نرجع الـ master tenant ID
        return MASTER_TENANT_ID;
    }

    public static void clear() {
        currentTenant.remove();
    }
}
