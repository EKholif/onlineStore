package com.onlineStore.admin.security;

import com.onlineStoreCom.entity.users.Role;
import com.onlineStoreCom.entity.users.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * <p>
 * This class wraps the application's User entity and adapts it to Spring Security's
 * UserDetails interface. It provides the necessary user information and authorities
 * required for authentication and authorization.
 */
public class StoreUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;
    private final User user;

    /**
     * Constructs a new StoreUserDetails with the specified user.
     * 
     * @param user the User entity to wrap
     */
    public StoreUserDetails(User user) {
        this.user = user;
    }

    /**
     * Returns the authorities granted to the user.
     * 
     * @return a collection of GrantedAuthority objects representing the user's roles
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = user.getRoles();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        
        return authorities;
    }

    /**
     * Returns the password used to authenticate the user.
     * 
     * @return the password
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username used to authenticate the user.
     * 
     * @return the username (email in this case)
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indicates whether the user's account has expired.
     * 
     * @return true if the user's account is valid (non-expired), false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * 
     * @return true if the user is not locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     * 
     * @return true if the user's credentials are valid (non-expired), false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     * 
     * @return true if the user is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return user.isEnable();
    }
    
    /**
     * Gets the image path of the user.
     * 
     * @return the user's image path
     */
    public String getImagePath() {
        return user.getImagePath();
    }

    /**
     * Gets the ID of the user.
     * 
     * @return the user's ID
     */
    public Integer getId() {
        return user.getId();
    }
    public Long getTenantId() {
        return user.getTenantId();
    }

    /**
     * Gets the full name of the user.
     * 
     * @return the user's full name
     */
    public String getFullName() {
        return user.getFullName();
    }

    /**
     * Gets the roles of the user.
     * 
     * @return the user's roles
     */
    public Set role() {
        return user.getRoles();
    }

    /**
     * Checks if the user has a specific role.
     * 
     * @param roleName the name of the role to check
     * @return true if the user has the specified role, false otherwise
     */
    public boolean hasRole(String roleName) {
        return user.hasRole(roleName);
    }

    public User getUser() {
        return this.user;
    }
}