package com.onlineStore.admin.audit;

import com.onlineStoreCom.entity.audit.TenantAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * AG-AUDIT-002: Repository for Tenant Audit Logs
 * <p>
 * Purpose:
 * - Query audit logs by user, tenant, action type
 * - Support pagination for admin panel
 * - Filter by date range for compliance reports
 */
@Repository
public interface TenantAuditLogRepository extends JpaRepository<TenantAuditLog, Long> {

    /**
     * Find all audit logs for a specific user
     */
    List<TenantAuditLog> findByUserIdOrderByCreatedAtDesc(Integer userId);

    /**
     * Find all audit logs for a specific tenant
     */
    List<TenantAuditLog> findByTenantIdOrderByCreatedAtDesc(Long tenantId);

    /**
     * Find audit logs by action type (e.g., SWITCH_TENANT, ACCESS_DENIED)
     */
    Page<TenantAuditLog> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);

    /**
     * Find recent audit logs with pagination
     */
    Page<TenantAuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find audit logs within date range
     */
    @Query("SELECT a FROM TenantAuditLog a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<TenantAuditLog> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * Find failed access attempts (for security monitoring)
     */
    @Query("SELECT a FROM TenantAuditLog a WHERE a.action = 'ACCESS_DENIED' ORDER BY a.createdAt DESC")
    Page<TenantAuditLog> findFailedAccessAttempts(Pageable pageable);

    /**
     * Count audit logs by user (for user activity summary)
     */
    long countByUserId(Integer userId);
}
