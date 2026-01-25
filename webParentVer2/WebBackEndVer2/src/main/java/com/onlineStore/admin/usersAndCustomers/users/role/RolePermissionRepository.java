package com.onlineStore.admin.usersAndCustomers.users.role;

import com.onlineStoreCom.entity.users.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AG-RBAC-PERM-003: Repository for Role Permissions
 * <p>
 * Purpose:
 * - Query permissions by role
 * - Support permission-based authorization
 * - Enable dynamic permission management in admin panel
 */
@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    /**
     * Find all permissions for a specific role
     */
    List<RolePermission> findByRoleId(Integer roleId);

    /**
     * Find a specific role-permission combination
     */
    @Query("SELECT rp FROM RolePermission rp WHERE rp.role.id = :roleId AND rp.permission = :permission")
    RolePermission findByRoleIdAndPermission(@Param("roleId") Integer roleId, @Param("permission") String permission);

    /**
     * Check if a role has a specific permission
     */
    @Query("SELECT COUNT(rp) > 0 FROM RolePermission rp WHERE rp.role.id = :roleId AND rp.permission = :permission")
    boolean hasPermission(@Param("roleId") Integer roleId, @Param("permission") String permission);

    /**
     * Delete all permissions for a role (for role cleanup)
     */
    void deleteByRoleId(Integer roleId);
}
