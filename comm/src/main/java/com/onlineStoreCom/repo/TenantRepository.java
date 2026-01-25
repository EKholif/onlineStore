package com.onlineStoreCom.repo;

import com.onlineStoreCom.entity.tenant.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByCode(String code);

    /**
     * AG-HIERARCHY-001: Find Child Tenants
     * <p>
     * Purpose:
     * - Used for Agency Admin access validation
     * - Returns all direct children of a tenant
     * - Supports tenant switcher dropdown
     */
    java.util.List<Tenant> findByParentId(Long parentId);
}
