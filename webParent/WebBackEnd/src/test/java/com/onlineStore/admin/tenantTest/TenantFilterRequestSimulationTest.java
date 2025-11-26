package com.onlineStore.admin.tenantTest;

import com.onlineStore.admin.security.tenant.TenantContext;
import com.onlineStoreCom.entity.brand.Brand;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class TenantFilterRequestSimulationTest {

    @Autowired
    private EntityManager em;

    @Test
    public void testTenantFilterAppliedOnRealEntityManager() {
        // Simulate a tenant in the current context
        Long simulatedTenantId = 1L;
        TenantContext.setTenantId(simulatedTenantId);

        // Unwrap the real Hibernate session
        Session session = em.unwrap(Session.class);
        session.enableFilter("tenantFilter")
                .setParameter("tenantId", TenantContext.getTenantId());

        System.out.println("🔥 Tenant filter enabled in test for tenant: " + simulatedTenantId);

        // Execute a query using the same EntityManager (like a real request would)
        List<Brand> brands = em.createQuery("FROM Brand", Brand.class).getResultList();

        // Check that all results belong to the simulated tenant
        assertTrue(brands.stream().allMatch(b -> b.getTenantId().equals(simulatedTenantId)));

        brands.forEach(b -> System.out.println(b.getName() + " -> Tenant: " + b.getTenantId()));

        // Clear the tenant context like the filter would at the end of a request
        TenantContext.clear();
    }
}
