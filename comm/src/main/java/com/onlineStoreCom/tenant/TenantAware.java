package com.onlineStoreCom.tenant;

public interface TenantAware {
    Long getTenantId();

    void setTenantId(Long tenantId);
}
