package com.onlineStoreCom.repo.base;

import com.onlineStoreCom.tenant.GlobalData;
import com.onlineStoreCom.tenant.TenantAware;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class BaseTenantRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseTenantRepository<T, ID> {

    public BaseTenantRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    @Override
    @Transactional
    public <S extends T> S save(S entity) {
        if (GlobalData.class.isAssignableFrom(getDomainClass())) {
            return super.save(entity);
        }

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new SecurityException("No Tenant Context set for save operation.");
        }

        if (entity instanceof TenantAware) {
            TenantAware tenantAware = (TenantAware) entity;
            Long entityTenantId = tenantAware.getTenantId();

            if (entityTenantId != null && !entityTenantId.equals(tenantId)) {
                throw new SecurityException("Cross-Tenant Write Attempt: Context=" + tenantId + ", Entity=" + entityTenantId);
            }
            tenantAware.setTenantId(tenantId);
        }
        return super.save(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        if (GlobalData.class.isAssignableFrom(getDomainClass())) {
            return super.findById(id);
        }

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new SecurityException("No Tenant Context set for findById operation.");
        }
        return findByIdAndTenantId(id, tenantId);
    }

    @Override
    public List<T> findAll() {
        if (GlobalData.class.isAssignableFrom(getDomainClass())) {
            return super.findAll();
        }

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new SecurityException("No Tenant Context set for findAll operation.");
        }
        return findAllByTenantId(tenantId);
    }

    @Override
    public Optional<T> findByIdAndTenantId(ID id, Long tenantId) {
        if (GlobalData.class.isAssignableFrom(getDomainClass())) {
            return super.findById(id);
        }
        return super.findOne((root, query, cb) -> cb.and(
                cb.equal(root.get("id"), id),
                cb.equal(root.get("tenantId"), tenantId)
        ));
    }

    @Override
    public List<T> findAllByTenantId(Long tenantId) {
        return super.findAll((root, query, cb) -> cb.equal(root.get("tenantId"), tenantId));
    }
}
