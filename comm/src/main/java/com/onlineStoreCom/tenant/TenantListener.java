package com.onlineStoreCom.tenant;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * Listener to automatically set the tenant ID on entities before saving.
 */
public class TenantListener {

    @PrePersist
    @PreUpdate
    public void setTenant(Object entity) {
        if (entity instanceof TenantAware) {
            TenantAware tenantAware = (TenantAware) entity;
            Long tenantId = TenantContext.getTenantId();
            if (tenantId != null) {
                tenantAware.setTenantId(tenantId);
            }
        }
    }

    /**
     * [ZERO-TRUST] Verify data INTEGRITY after loading from DB.
     * If I am Tenant A, and I somehow loaded Tenant B's data, EXECUTE ORDER 66
     * (Exception).
     */
    @jakarta.persistence.PostLoad
    public void verifyDataIntegrity(Object entity) {
        // [AG-TEN-SEC-006] Allow Global Data (Countries, States, etc.)
        if (entity instanceof GlobalData) {
            return;
        }

        if (entity instanceof TenantAware) {
            TenantAware tenantAware = (TenantAware) entity;
            Long entityTenantId = tenantAware.getTenantId();
            Long currentContextId = TenantContext.getTenantId();

            // 1. If Entity is Global (null tenantId) OR System (0), allow it.
            if (entityTenantId == null || entityTenantId == 0) {
                return;
            }

            // 2. If System Process (No Context), allow it (be careful here).
            if (currentContextId == null) {
                return;
            }

            // 3. STRICT CHECK: Context MUST match Entity
            if (!entityTenantId.equals(currentContextId)) {
                String msg = "⛔ SECURITY ALERT: Data Guard Blocked Access! " +
                        "Current Tenant [" + currentContextId + "] tried to access data of Tenant [" + entityTenantId
                        + "]";
                System.err.println(msg);
                throw new SecurityException(msg);
            }

            // [TRACING 4/4] Data Returned
            // System.out.println(" > 📤 [TRACING] Data Verified: Owner=" + entityTenantId +
            // " vs Requester=" + currentContextId);
        }
    }
}
