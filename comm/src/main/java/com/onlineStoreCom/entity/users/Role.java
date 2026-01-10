package com.onlineStoreCom.entity.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onlineStoreCom.entity.setting.subsetting.HierarchicalEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.onlineStoreCom.tenant.GlobalData;

@Entity
@Table(name = "role")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@org.hibernate.annotations.Filter(name = "tenantFilter", condition = "(tenant_id = :tenantId or tenant_id = 0 or tenant_id is null)")
@ExcludeSuperclassListeners
public class Role extends HierarchicalEntity<Role> implements GlobalData {

    @Column(name = "name", length = 40, nullable = false, unique = false)
    private String name;

    @Column(name = "descrption", length = 150, nullable = false)
    private String descrption;

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

    @Override
    public String toString() {
        return this.name;

    }
}
