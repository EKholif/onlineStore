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

import static org.testng.AssertJUnit.assertEquals;

@SpringBootTest
@Transactional
public class TenantFilterCheckTest {

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testTenantFilterWorks() {
        // 1️⃣ تحديد tenantId الحالي
        Long tenantId = 123L;
        TenantContext.setTenantId(tenantId);
        System.out.println("TenantContext: setTenantId = " + TenantContext.getTenantId());

        // 2️⃣ تفعيل الفلتر قبل أي query
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
        System.out.println("Filter enabled: " + session.getEnabledFilter("tenantFilter"));

        // 3️⃣ نفذ query للمنتجات
        List<Product> products = productRepository.findAll();

        // 4️⃣ طباعة النتائج للتأكد من tenantId
        for (Product p : products) {
            System.out.println("Product tenantId: " + p.getTenantId() + " Name: " + p.getName());
        }

        // 5️⃣ التأكد من كل النتائج tenantId بتاعها = tenantId الحالي
        for (Product p : products) {
            assertEquals(tenantId, p.getTenantId());
        }

        // 6️⃣ تنظيف TenantContext بعد الاختبار
        TenantContext.clear();
        System.out.println("TenantContext after clear: " + TenantContext.getTenantId());
    }
}
