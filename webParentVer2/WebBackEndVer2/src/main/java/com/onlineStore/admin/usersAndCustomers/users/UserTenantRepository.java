package com.onlineStore.admin.usersAndCustomers.users;

import com.onlineStoreCom.entity.users.UserTenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AG-RBAC-REPO-001: UserTenant Repository for direct tenant queries
 * <p>
 * Purpose:
 * - Provides direct access to user_tenants table
 * - Bypasses lazy loading issues in tenant access validation
 * - Used as fallback in canAccessTenant() method
 * <p>
 * Business Impact:
 * - Ensures Platform Admin can always access system
 * - Prevents access denied errors due to lazy loading
 */
@Repository
public interface UserTenantRepository extends JpaRepository<UserTenant, Long> {

    /**
     * Get all tenant IDs for a given user ID
     * Direct query bypasses entity lazy loading issues
     *
     * @param userId User ID to query
     * @return List of tenant IDs the user has access to
     */
    @Query("SELECT ut.tenant.id FROM UserTenant ut WHERE ut.user.id = :userId")
    List<Long> findTenantIdsByUserId(@Param("userId") Integer userId);

    /**
     * Get all UserTenant entries for a user
     *
     * @param userId User ID
     * @return List of UserTenant relationships
     */
    List<UserTenant> findByUserId(Integer userId);

    /**
     * Check if a user has access to a specific tenant
     *
     * @param userId   User ID
     * @param tenantId Tenant ID
     * @return true if relationship exists
     */
    @Query("SELECT COUNT(ut) > 0 FROM UserTenant ut WHERE ut.user.id = :userId AND ut.tenant.id = :tenantId")
    boolean existsByUserIdAndTenantId(@Param("userId") Integer userId, @Param("tenantId") Long tenantId);
}
