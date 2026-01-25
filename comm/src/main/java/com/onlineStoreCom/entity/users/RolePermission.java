package com.onlineStoreCom.entity.users;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * AG-RBAC-PERM-001: Permission Entity for Fine-Grained Access Control
 * <p>
 * Purpose:
 * - Define granular permissions (e.g., MANAGE_TENANTS, VIEW_AUDIT_LOGS)
 * - Map permissions to roles via RolePermission table
 * - Enable permission-based authorization checks
 * <p>
 * Business Impact:
 * - Flexible RBAC system
 * - Principle of least privilege enforcement
 * - Support for custom tenant-specific permissions
 * <p>
 * Examples:
 * - MANAGE_TENANTS: Create, edit, delete tenants (Platform Admin only)
 * - SWITCH_TENANT: Switch to child tenants (Platform/Agency Admin)
 * - VIEW_AUDIT_LOGS: Access security audit logs (Platform/Agency Admin)
 * - IMPERSONATE_USER: Impersonate other users (Platform Admin only)
 * - MANAGE_USERS: Create, edit users within tenant (All admins)
 */
@Entity
@Table(name = "role_permissions")
public class RolePermission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Associated role
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /**
     * Permission name (e.g., MANAGE_TENANTS, SWITCH_TENANT)
     */
    @Column(nullable = false, length = 100)
    private String permission;

    /**
     * Optional: Permission scope (e.g., OWN_TENANT, CHILD_TENANTS, ALL_TENANTS)
     */
    @Column(length = 50)
    private String scope;

    // Constructors

    public RolePermission() {
    }

    public RolePermission(Role role, String permission) {
        this.role = role;
        this.permission = permission;
    }

    public RolePermission(Role role, String permission, String scope) {
        this.role = role;
        this.permission = permission;
        this.scope = scope;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "RolePermission{" +
                "id=" + id +
                ", permission='" + permission + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
