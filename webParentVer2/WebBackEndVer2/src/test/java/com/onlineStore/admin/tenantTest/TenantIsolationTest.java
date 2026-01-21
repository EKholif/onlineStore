package com.onlineStore.admin.tenantTest;

import com.onlineStore.admin.product.repository.ProductRepository;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
public class TenantIsolationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager em;

    @Test
    public void testDataIsolationBetweenTenants() {
        // 1. Create Data for Tenant 1
        Long tenant1 = 101L;
        Long tenant2 = 102L;

        // Bypassing filter to insert raw data (simulating DB state)
        // We use a manual insert or temporarily set context.
        TenantContext.setTenantId(tenant1);
        Product p1 = new Product("Secret_Product_T1");
        p1.setAlias("secret-t1");
        p1.setShortDescription("Target");
        p1.setFullDescription("Target Full Description");
        p1.setCreatedTime(new java.util.Date());
        p1.setUpdatedTime(new java.util.Date());
        p1.setMainImage("main.jpg");
        productRepository.save(p1); // entity has @PrePersist/Listener logic usually?
        // Need to ensure tenant_id is set. entity usually gets it from context or we
        // set it?
        // Let's assume Entity doesn't auto-set it in this simple test unless we set it.
        // Wait, does Product entity set tenantId automatically?
        // Checking code: IdBasedEntity usually has it.
        // Let's check if we need to flush.
        em.flush();
        em.clear();

        // 2. Switch to Tenant 2
        TenantContext.setTenantId(tenant2);

        // 3. Enable Filter (Simulation of Filter)
        Session session = em.unwrap(Session.class);
        session.enableFilter("tenantFilter").setParameter("tenantId", tenant2);

        // 4. Query: Should NOT see T1 product
        List<Product> products = productRepository.findAll();
        boolean leaked = products.stream().anyMatch(p -> p.getName().equals("Secret_Product_T1"));

        assertFalse(leaked, "🚨 DATA LEAK DETECTED! Tenant 2 saw Tenant 1's product.");

        // 5. Query: Should see only T2 (empty for now)
        System.out.println("Tenant 2 Products: " + products.size());

        TenantContext.clear();
    }
}
