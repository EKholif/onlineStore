package com.onlineStoreCom.entity.users;

import com.onlineStoreCom.entity.tenant.Tenant;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user_tenants", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "tenant_id"})
})
public class UserTenant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    // [AG-SAAS-HIERARCHY] In the future, we can add specific Roles per tenant here.
    // For now, we rely on the User's global roles but scoped to this tenant access.
    // Or we can map a Role here if required.
    // private Role role; 

    public UserTenant() {
    }

    public UserTenant(User user, Tenant tenant) {
        this.user = user;
        this.tenant = tenant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTenant that = (UserTenant) o;
        return Objects.equals(user, that.user) && Objects.equals(tenant, that.tenant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, tenant);
    }
}
