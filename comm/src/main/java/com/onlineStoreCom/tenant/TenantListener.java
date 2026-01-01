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
}
