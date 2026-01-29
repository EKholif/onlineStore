package com.onlineStore.admin.security;

import com.onlineStore.admin.usersAndCustomers.users.UserRepository;
import com.onlineStoreCom.entity.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * <p>
 * This service is responsible for loading user-specific data during
 * authentication.
 * It retrieves user information from the database and wraps it in a UserDetails
 * implementation for Spring Security.
 * 
 * @Service - Implicitly marked as a service by implementing UserDetailsService
 */
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Locates the user based on the username (in this case, email).
     * 
     * @param email the email address identifying the user whose data is required
     * @return a fully populated UserDetails object (never null)
     * @throws UsernameNotFoundException if the user could not be found or the user
     *                                   has no authorities
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Try Scoped Search (Current Tenant)
        User user = userRepository.findByEmail(email);

        if (user != null) {
            return new StoreBackendUserDetails(user);
        }

        // 2. Fallback: Global Search if on Platform (Tenant 0)
        // This allows correct login for Tenant Users via the Platform Login Page
        Long currentTenantId = com.onlineStoreCom.tenant.TenantContext.getTenantId();
        if (currentTenantId != null && currentTenantId == 0L) {
            System.out.println("🔐 [UserDetailsService] User not found in Scope 0. Trying Global Search for: " + email);
            user = userRepository.findByEmailGeneric(email);
            if (user != null) {
                System.out.println("   > Found Globally! User Tenant: " + user.getTenantId());
                return new StoreBackendUserDetails(user);
            }
        }

        throw new UsernameNotFoundException("Could not find user with email: " + email);
    }
}
