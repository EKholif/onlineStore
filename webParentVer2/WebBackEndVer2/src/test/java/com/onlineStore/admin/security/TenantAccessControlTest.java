package com.onlineStore.admin.security;

import com.onlineStore.admin.usersAndCustomers.users.servcies.UserService;
import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.entity.users.Role;
import com.onlineStoreCom.entity.users.User;
import com.onlineStoreCom.entity.users.UserTenant;
import com.onlineStoreCom.repo.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AG-TEST-ACCESS-001: Unit Tests for Tenant Access Control
 * <p>
 * Purpose:
 * - Verify canAccessTenant() logic for all user roles
 * - Test hierarchy-based access rules
 * - Validate N:N tenant mappings
 * <p>
 * Test Scenarios:
 * - Platform Admin can access ALL tenants
 * - Agency Admin can access own + children + N:N mapped
 * - Tenant Admin can access ONLY own tenant
 * - Unauthorized access returns false
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Tenant Access Control Tests")
public class TenantAccessControlTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TenantRepository tenantRepo;

    private Tenant masterTenant;
    private Tenant agencyTenant;
    private Tenant childTenant1;
    private Tenant childTenant2;

    private User platformAdmin;
    private User agencyAdmin;
    private User tenantAdmin;

    @BeforeEach
    void setUp() {
        // Create tenant hierarchy
        masterTenant = createTenant(0L, "SaaS Master", "MASTER", null, 0);
        agencyTenant = createTenant(1L, "Agency Tenant", "AGENCY", masterTenant, 1);
        childTenant1 = createTenant(2L, "Child Tenant 1", "CHILD1", agencyTenant, 2);
        childTenant2 = createTenant(3L, "Child Tenant 2", "CHILD2", agencyTenant, 2);

        // Create users with different roles
        platformAdmin = createUserWithTenants("platform@admin.com", masterTenant);
        agencyAdmin = createUserWithTenants("agency@admin.com", agencyTenant);
        tenantAdmin = createUserWithTenants("tenant@admin.com", childTenant1);
    }

    // ============================================================================
    // Platform Admin Tests
    // ============================================================================

    @Test
    @DisplayName("Platform Admin (Tenant 0) can access ALL tenants")
    void platformAdmin_CanAccessAllTenants() {
        // Platform Admin should access master (0)
        assertTrue(userService.canAccessTenant(platformAdmin, masterTenant.getId()),
                "Platform Admin should access Master Tenant (0)");

        // Platform Admin should access agency (1)
        assertTrue(userService.canAccessTenant(platformAdmin, agencyTenant.getId()),
                "Platform Admin should access Agency Tenant (1)");

        // Platform Admin should access children (2, 3)
        assertTrue(userService.canAccessTenant(platformAdmin, childTenant1.getId()),
                "Platform Admin should access Child Tenant 1 (2)");

        assertTrue(userService.canAccessTenant(platformAdmin, childTenant2.getId()),
                "Platform Admin should access Child Tenant 2 (3)");
    }

    @Test
    @DisplayName("Platform Admin can access any arbitrary tenant ID")
    void platformAdmin_CanAccessArbitraryTenantId() {
        // Even non-existent tenant IDs should return true for Platform Admin
        // (actual existence check happens in controller/filter)
        assertTrue(userService.canAccessTenant(platformAdmin, 999L),
                "Platform Admin should pass access check for any tenant ID");
    }

    // ============================================================================
    // Agency Admin Tests
    // ============================================================================

    @Test
    @DisplayName("Agency Admin can access own tenant")
    void agencyAdmin_CanAccessOwnTenant() {
        assertTrue(userService.canAccessTenant(agencyAdmin, agencyTenant.getId()),
                "Agency Admin should access own tenant (1)");
    }

    @Test
    @DisplayName("Agency Admin can access child tenants")
    void agencyAdmin_CanAccessChildTenants() {
        assertTrue(userService.canAccessTenant(agencyAdmin, childTenant1.getId()),
                "Agency Admin should access Child Tenant 1 (2)");

        assertTrue(userService.canAccessTenant(agencyAdmin, childTenant2.getId()),
                "Agency Admin should access Child Tenant 2 (3)");
    }

    @Test
    @DisplayName("Agency Admin CANNOT access parent tenant (Master)")
    void agencyAdmin_CannotAccessParentTenant() {
        assertFalse(userService.canAccessTenant(agencyAdmin, masterTenant.getId()),
                "Agency Admin should NOT access Master Tenant (0)");
    }

    @Test
    @DisplayName("Agency Admin with N:N mapping can access mapped tenant")
    void agencyAdmin_CanAccessNtoNMappedTenant() {
        // Add N:N mapping to Child Tenant 2
        addTenantToUser(agencyAdmin, childTenant2);

        assertTrue(userService.canAccessTenant(agencyAdmin, childTenant2.getId()),
                "Agency Admin should access N:N mapped Child Tenant 2");
    }

    // ============================================================================
    // Tenant Admin Tests
    // ============================================================================

    @Test
    @DisplayName("Tenant Admin can access ONLY own tenant")
    void tenantAdmin_CanAccessOwnTenantOnly() {
        // Can access own tenant
        assertTrue(userService.canAccessTenant(tenantAdmin, childTenant1.getId()),
                "Tenant Admin should access own tenant (2)");

        // Cannot access parent
        assertFalse(userService.canAccessTenant(tenantAdmin, agencyTenant.getId()),
                "Tenant Admin should NOT access parent Agency Tenant (1)");

        // Cannot access master
        assertFalse(userService.canAccessTenant(tenantAdmin, masterTenant.getId()),
                "Tenant Admin should NOT access Master Tenant (0)");

        // Cannot access sibling
        assertFalse(userService.canAccessTenant(tenantAdmin, childTenant2.getId()),
                "Tenant Admin should NOT access sibling Child Tenant 2 (3)");
    }

    // ============================================================================
    // Edge Cases & Validation Tests
    // ============================================================================

    @Test
    @DisplayName("canAccessTenant returns false for null user")
    void canAccessTenant_NullUser_ReturnsFalse() {
        assertFalse(userService.canAccessTenant(null, childTenant1.getId()),
                "Null user should return false");
    }

    @Test
    @DisplayName("canAccessTenant returns false for null tenant ID")
    void canAccessTenant_NullTenantId_ReturnsFalse() {
        assertFalse(userService.canAccessTenant(platformAdmin, null),
                "Null tenant ID should return false");
    }

    @Test
    @DisplayName("canAccessTenant returns false for user with no tenants")
    void canAccessTenant_UserWithNoTenants_ReturnsFalse() {
        User userWithNoTenants = new User();
        userWithNoTenants.setEmail("no-tenant@user.com");
        userWithNoTenants.setTenants(new HashSet<>());

        assertFalse(userService.canAccessTenant(userWithNoTenants, childTenant1.getId()),
                "User with no tenants should return false");
    }

    // ============================================================================
    // getAccessibleTenants() Tests
    // ============================================================================

    @Test
    @DisplayName("Platform Admin getAccessibleTenants returns ALL tenants")
    void platformAdmin_GetAccessibleTenants_ReturnsAll() {
        List<Tenant> accessible = userService.getAccessibleTenants(platformAdmin);

        // Should return all tenants in database
        assertTrue(accessible.size() >= 4,
                "Platform Admin should see at least 4 tenants (Master, Agency, 2 Children)");

        assertTrue(accessible.stream().anyMatch(t -> t.getId().equals(masterTenant.getId())),
                "Should include Master Tenant");
        assertTrue(accessible.stream().anyMatch(t -> t.getId().equals(agencyTenant.getId())),
                "Should include Agency Tenant");
    }

    @Test
    @DisplayName("Agency Admin getAccessibleTenants returns own + children")
    void agencyAdmin_GetAccessibleTenants_ReturnsOwnAndChildren() {
        List<Tenant> accessible = userService.getAccessibleTenants(agencyAdmin);

        // Should include agency + 2 children = 3 tenants
        assertEquals(3, accessible.size(),
                "Agency Admin should see 3 tenants (Own + 2 Children)");

        assertTrue(accessible.stream().anyMatch(t -> t.getId().equals(agencyTenant.getId())),
                "Should include own Agency Tenant");
        assertTrue(accessible.stream().anyMatch(t -> t.getId().equals(childTenant1.getId())),
                "Should include Child Tenant 1");
        assertTrue(accessible.stream().anyMatch(t -> t.getId().equals(childTenant2.getId())),
                "Should include Child Tenant 2");

        assertFalse(accessible.stream().anyMatch(t -> t.getId().equals(masterTenant.getId())),
                "Should NOT include Master Tenant");
    }

    @Test
    @DisplayName("Tenant Admin getAccessibleTenants returns ONLY own tenant")
    void tenantAdmin_GetAccessibleTenants_ReturnsOwnOnly() {
        List<Tenant> accessible = userService.getAccessibleTenants(tenantAdmin);

        assertEquals(1, accessible.size(),
                "Tenant Admin should see only 1 tenant (own)");

        assertEquals(childTenant1.getId(), accessible.get(0).getId(),
                "Should return own Child Tenant 1");
    }

    // ============================================================================
    // Helper Methods
    // ============================================================================

    private Tenant createTenant(Long id, String name, String code, Tenant parent, int level) {
        Tenant tenant = new Tenant();
        tenant.setId(id);
        tenant.setName(name);
        tenant.setCode(code);
        tenant.setParent(parent);
        tenant.setLevel(level);
        tenant.setActive(true);
        return tenantRepo.save(tenant);
    }

    private User createUserWithTenants(String email, Tenant... tenants) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("encoded_password");
        user.setEnabled(true);

        // Add roles (simplified for testing)
        Set<Role> roles = new HashSet<>();
        Role role = new Role("Admin");
        roles.add(role);
        user.setRoles(roles);

        // Add tenants
        Set<UserTenant> userTenants = new HashSet<>();
        for (Tenant tenant : tenants) {
            UserTenant ut = new UserTenant();
            ut.setUser(user);
            ut.setTenant(tenant);
            // Note: isHome/isCurrent not available in UserTenant entity
            userTenants.add(ut);
        }
        user.setTenants(userTenants);

        return user;
    }

    private void addTenantToUser(User user, Tenant tenant) {
        UserTenant ut = new UserTenant();
        ut.setUser(user);
        ut.setTenant(tenant);
        // Note: isHome/isCurrent not available in UserTenant entity
        user.getTenants().add(ut);
    }
}
