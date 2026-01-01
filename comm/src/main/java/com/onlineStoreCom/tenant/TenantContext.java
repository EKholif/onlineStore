package com.onlineStoreCom.tenant;

/**
 * Holds the current Tenant ID for the request/thread.
 * Moved to 'comm' module to be accessible by Entities and Listeners.
 */
public class TenantContext {

    // [AG-TEN-ARCH-003] Context now holds full TenantDetails (ID + Name)
    private static final ThreadLocal<TenantDetails> currentTenant = new ThreadLocal<>();

    public static TenantDetails getTenant() {
        return currentTenant.get();
    }

    public static void setTenant(TenantDetails tenant) {
        currentTenant.set(tenant);
    }

    public static Long getTenantId() {
        TenantDetails details = currentTenant.get();
        return (details != null) ? details.getId() : null;
    }

    // Legacy support / Convenience
    public static void setTenantId(Long tenantId) {
        // Fallback name if only ID is provided (e.g. from old tests)
        currentTenant.set(new TenantDetails(tenantId, "unknown-" + tenantId));
    }

    public static String getTenantName() {
        TenantDetails details = currentTenant.get();
        return (details != null) ? details.getName() : null;
    }

    public static void clear() {
        currentTenant.remove();
    }
}
