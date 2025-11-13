package com.onlineStore.admin.test.counteryTest.userTest.tenant;

import com.onlineStore.admin.product.repository.ProductRepository;
import com.onlineStore.admin.security.tenant.TenantContext;
import com.onlineStoreCom.entity.product.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TenantFilterIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final Long TENANT_ID_1 = 8247009068765685744L;
    private final Long TENANT_ID_0 = 0L;

    @BeforeEach
    public void setupTenantContext() {
        // محاكاة تسجيل الدخول لمستخدم
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user1", "password", List.of())
        );

        // نخزن tenantId في ThreadLocal
        TenantContext.setTenantId(TENANT_ID_1);
    }

    @Test
    @Transactional
    public void testTenantFilter() {
        // نفعل فلتر Hibernate على الـ Session
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("tenantFilter").setParameter("tenantId", TenantContext.getTenantId());
        System.out.println("Tenant filter enabled: " + session.getEnabledFilter("tenantFilter"));

        // نجيب كل المنتجات
        List<Product> products = productRepository.findAll();

        // نطبع النتائج عشان نتاكد الفلتر شغال
        System.out.println("Current tenant: " + TenantContext.getTenantId());
        for (Product p : products) {
            System.out.println("Product tenantId: " + p.getTenantId() + " Name: " + p.getName());
            assertEquals(TENANT_ID_1, p.getTenantId(), "Product tenantId يجب أن يكون مطابق للـ tenant الحالي");
        }

        // تنظيف ThreadLocal
        TenantContext.clear();
    }

    @Test
    @Transactional
    public void testNoTenantFilterReturnsNothing() {
        // نخلي ThreadLocal فارغ
        TenantContext.clear();

        List<Product> products = productRepository.findAll();

        // كل المنتجات ترجع لأن الفلتر مش مفعّل
        for (Product p : products) {
            System.out.println("Product tenantId: " + p.getTenantId() + " Name: " + p.getName());
        }
    }
}
