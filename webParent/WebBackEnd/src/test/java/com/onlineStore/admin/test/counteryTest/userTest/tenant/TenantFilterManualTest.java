package com.onlineStore.admin.test.counteryTest.userTest.tenant;

import com.onlineStore.admin.product.repository.ProductRepository;
import com.onlineStore.admin.security.tenant.TenantContext;
import com.onlineStoreCom.entity.product.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class TenantFilterManualTest {

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testEnableTenantFilterManually() {
        Long tenantId = 8247009068765685744L;
        TenantContext.setTenantId(tenantId);

        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
        System.out.println("Tenant filter enabled: " + session.getEnabledFilter("tenantFilter"));

        List<Product> products = productRepository.findAll();
        for (Product p : products) {
            System.out.println("Product tenantId: " + p.getTenantId() + " Name: " + p.getName());
            assertEquals(tenantId, p.getTenantId(), "Found product with wrong tenantId!");
        }

        TenantContext.clear();
    }
}
