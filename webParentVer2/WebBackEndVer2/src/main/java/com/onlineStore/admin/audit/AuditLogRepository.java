package com.onlineStore.admin.audit;

import com.onlineStoreCom.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Find all logs (Super Admin)
    Page<AuditLog> findAll(Pageable pageable);

    // Find logs by tenant
    Page<AuditLog> findByTenantId(String tenantId, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE " +
            "(:keyword IS NULL OR a.module LIKE %:keyword% OR a.action LIKE %:keyword% OR a.userEmail LIKE %:keyword%)")
    Page<AuditLog> findAllWithFilter(String keyword, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.tenantId = :tenantId AND " +
            "(:keyword IS NULL OR a.module LIKE %:keyword% OR a.action LIKE %:keyword% OR a.userEmail LIKE %:keyword%)")
    Page<AuditLog> findByTenantIdWithFilter(String tenantId, String keyword, Pageable pageable);
}
