package com.onlineStore.admin.security;

import com.onlineStore.admin.usersAndCustomers.users.servcies.UserService;
import com.onlineStoreCom.entity.users.Role;
import com.onlineStoreCom.entity.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * AG-RBAC-EVAL-001: Tenant-Aware Permission Evaluator
 * <p>
 * Purpose:
 * - Custom PermissionEvaluator for Spring Security
 * - Integrates with @PreAuthorize("hasPermission(null, 'PERMISSION_NAME')")
 * - Checks if user's roles have the requested permission
 * <p>
 * Business Impact:
 * - Fine-grained access control per endpoint
 * - Prevents unauthorized actions (e.g., MANAGE_TENANTS by Tenant Admin)
 * - Supports custom permissions per role
 * <p>
 * Example Usage:
 * <pre>
 * @PreAuthorize("hasPermission(null, 'MANAGE_TENANTS')")
 * public void createTenant() { ... }
 * </pre>
 */
@Component
public class TenantPermissionEvaluator implements PermissionEvaluator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantPermissionEvaluator.class);

    @Autowired
    @Lazy // AG-RBAC-FIX-001: Break circular dependency with UserService
    private UserService userService;


    /**
     * AG-RBAC-CHECK-002: Permission Check Implementation
     *
     * @param authentication     Current user authentication
     * @param targetDomainObject Not used (pass null)
     * @param permission         Permission string (e.g., "MANAGE_TENANTS")
     * @return true if user has permission, false otherwise
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (!(permission instanceof String)) {
            LOGGER.warn("Permission must be a String, got: {}", permission.getClass());
            return false;
        }

        String permissionName = (String) permission;

        // Extract user from authentication
        User user = getUserFromAuthentication(authentication);
        if (user == null) {
            LOGGER.warn("User not found for authentication: {}", authentication.getName());
            return false;
        }

        // Check if any of user's roles have this permission
        for (Role role : user.getRoles()) {
            if (role.hasPermission(permissionName)) {
                LOGGER.debug("User {} has permission {} via role {}",
                        user.getEmail(), permissionName, role.getName());
                return true;
            }
        }

        LOGGER.debug("User {} DENIED permission {}", user.getEmail(), permissionName);
        return false;
    }

    /**
     * AG-RBAC-CHECK-003: Permission Check with Target ID
     * <p>
     * Not used in current implementation, but required by PermissionEvaluator interface
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        // For future use (e.g., check permission on specific tenant ID)
        return hasPermission(authentication, null, permission);
    }

    /**
     * Extract User entity from Spring Security Authentication
     */
    private User getUserFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            return userService.getByEmail(userDetails.getUsername());
        }

        if (principal instanceof String) {
            return userService.getByEmail((String) principal);
        }

        return null;
    }
}
