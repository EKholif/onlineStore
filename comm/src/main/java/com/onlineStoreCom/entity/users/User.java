package com.onlineStoreCom.entity.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table(name = "'user'")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class User extends IdBasedEntity {

    @Column(name = "email", length = 85, nullable = false)
    private String email;
    @Column(name = "password", length = 85, nullable = false)

    private String password;
    @Column(name = "first_name", length = 85, nullable = false)

    private String firstName;

    @Column(name = "last_name", length = 85, nullable = false)

    private String lastName;
    @Column(name = "enabled")
    private boolean enabled;
    @Column(name = "user_bio")
    private String user_bio;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String email) {

        this.email = email;
    }

    public String getUser_bio() {
        return user_bio;
    }

    public void setUser_bio(String user_bio) {
        this.user_bio = user_bio;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getfirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                " id = '" + id + '\'' +
                " firstName = '" + firstName + '\'' +

                "lastName = '" + lastName + '\'' +

                " status = '" + enabled + '\'' +
                " TenantId = '" + getTenantId() + '\'' +
                '}';
    }

    @Column(name = "photos", length = 64)
    private String photos;

    @Transient
    public String getImagePath() {
        if (id == null || photos == null)
            return "/images/bob.png";

        // AG-ASSET-PATH-004: Standardized tenant asset path
        // Returns: /tenants/{tenantId}/assets/users/{id}/{filename}
        return "/tenants/" + this.getTenantId() + "/assets/users/" + this.id + "/" + this.photos;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    @Transient
    public String getImageDir() {
        if (id == null)
            return null; // Return null instead of dummy path for directory cleaning safety

        // AG-ASSET-PATH-004: Standardized tenant asset path
        // Returns: tenants/{tenantId}/assets/users/{id}/
        return "tenants/" + this.getTenantId() + "/assets/users/" + this.id + "/";
    }

    public String getFullName() {
        return firstName + " " + lastName;

    }

    public boolean hasRole(String roleName) {
        Iterator<Role> iterator = roles.iterator();

        while (iterator.hasNext()) {
            Role role = iterator.next();
            if (role.getName().equals(roleName)) {
                return true;
            }
        }

        return false;
    }

}
