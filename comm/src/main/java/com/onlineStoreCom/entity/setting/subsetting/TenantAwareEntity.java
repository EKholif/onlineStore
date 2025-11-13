package com.onlineStoreCom.entity.setting.subsetting;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.TenantId;

@MappedSuperclass
@FilterDef(
        name = "tenantFilter",
        parameters = @ParamDef(name = "tenantId", type = Long.class)
)
@Filter(
        name = "tenantFilter",
        condition = "tenant_id = :tenantId"
)
public abstract class TenantAwareEntity  {
    @Column(name = "tenant_id", nullable = false, updatable = false )

    private Long tenantId;

    @PrePersist
    public void prePersist() {
        if (tenantId == null) {
            Long currentTenant = getTenantId();
            tenantId = (currentTenant != null) ? currentTenant : 0L;
        }
    }



    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

        }



