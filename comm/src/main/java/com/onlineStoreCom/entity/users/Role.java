package com.onlineStoreCom.entity.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onlineStoreCom.entity.setting.subsetting.HierarchicalEntity;
import com.onlineStoreCom.tenant.GlobalData;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "role")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
// AG-SHARED-DATA-001: Role is GlobalData - no tenant filter needed
@ExcludeSuperclassListeners
public class Role extends HierarchicalEntity<Role> implements GlobalData {

    @Column(name = "name", length = 40, nullable = false, unique = false)
    private String name;

    @Column(name = "descrption", length = 150, nullable = false)
    private String descrption;

    /**
     * AG-RBAC-PERM-002: Role Permissions Collection
     * <p>
     * Purpose:
     * - Maps role to its associated permissions
     * - Enables hasPermission() helper for authorization checks
     * - Supports dynamic permission management
     */
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<RolePermission> permissions = new HashSet<>();

    @Override
    public Integer getId() {
        return super.getId();
    }

    public Role(String roleName) {
        super();
        this.name = roleName;
    }

    public Role() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescrption() {
        return descrption;
    }

    public void setDescrption(String descrption) {
        this.descrption = descrption;
    }

    public Role(String name, String descrption) {
        this.name = name;
        this.descrption = descrption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Role role = (Role) o;
        return Objects.equals(getId(), role.getId()) && Objects.equals(name, role.name)
                && Objects.equals(descrption, role.descrption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), name, descrption);
    }

    public Set<RolePermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<RolePermission> permissions) {
        this.permissions = permissions;
    }

    /**
     * AG-RBAC-CHECK-001: Permission Check Helper
     * <p>
     * Purpose:
     * - Fast permission lookup for authorization checks
     * - Used by PermissionEvaluator and @PreAuthorize
     * <p>
     * Example:
     * - if (role.hasPermission("MANAGE_TENANTS")) { ... }
     */
    public boolean hasPermission(String permissionName) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }
        return permissions.stream()
                .anyMatch(p -> p.getPermission().equalsIgnoreCase(permissionName));
    }

    @Override
    public String toString() {
        return this.name;

    }
}
