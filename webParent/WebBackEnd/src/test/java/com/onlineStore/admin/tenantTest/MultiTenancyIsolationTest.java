package com.onlineStore.admin.tenantTest;

import com.onlineStore.admin.usersAndCustomers.users.UserRepository;
import com.onlineStoreCom.entity.users.User;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * [AG-TEN-REQ-003] Multi-Tenancy Isolation Test
 * Verifies that data created by one tenant is NOT visible to another.
 */
@SpringBootTest
@Transactional
public class MultiTenancyIsolationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Test
    public void testTwoTenantsDataIsolation() {
        // [Scenario]: 2 Different Tenants (A and B)
        Long tenantA = 100L;
        Long tenantB = 200L;

        // 1. Create Data as Tenant A
        TenantContext.setTenantId(tenantA);
        createTestUser("UserA@test.com", "User A", tenantA);
        TenantContext.clear();

        // 2. Create Data as Tenant B
        TenantContext.setTenantId(tenantB);
        createTestUser("UserB@test.com", "User B", tenantB);
        TenantContext.clear();

        // 3. Verify Isolation for Tenant A
        // Expectation: Should ONLY see User A, NOT User B
        TenantContext.setTenantId(tenantA);
        enableHibernateFilter(tenantA);

        List<User> usersForA = userRepository.findAll();
        System.out.println("🔥 Tenant A Visible Users: " + usersForA.size());
        usersForA.forEach(u -> System.out.println("   - " + u.getEmail() + " (Tenant: " + u.getTenantId() + ")"));

        Assertions.assertTrue(usersForA.stream().anyMatch(u -> u.getEmail().equals("UserA@test.com")),
                "Tenant A should see their own user");
        Assertions.assertTrue(usersForA.stream().noneMatch(u -> u.getEmail().equals("UserB@test.com")),
                "Tenant A should NOT see Tenant B user");

        TenantContext.clear();

        // 4. Verify Isolation for Tenant B
        // Expectation: Should ONLY see User B, NOT User A
        TenantContext.setTenantId(tenantB);
        enableHibernateFilter(tenantB);

        List<User> usersForB = userRepository.findAll();
        System.out.println("🔥 Tenant B Visible Users: " + usersForB.size());
        usersForB.forEach(u -> System.out.println("   - " + u.getEmail() + " (Tenant: " + u.getTenantId() + ")"));

        Assertions.assertTrue(usersForB.stream().anyMatch(u -> u.getEmail().equals("UserB@test.com")),
                "Tenant B should see their own user");
        Assertions.assertTrue(usersForB.stream().noneMatch(u -> u.getEmail().equals("UserA@test.com")),
                "Tenant B should NOT see Tenant A user");

        TenantContext.clear();
    }

    private void createTestUser(String email, String firstName, Long tenantId) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName("Test");
        user.setPassword("password");
        user.setTenantId(tenantId);
        userRepository.save(user);
        // Force flush to ensure data is in DB before clearing context
        userRepository.flush();
    }

    private void enableHibernateFilter(Long tenantId) {
        Session session = em.unwrap(Session.class);
        session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
    }
}
