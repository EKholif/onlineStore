package com.onlineStore.admin.tenantTest;

import com.onlineStore.admin.security.tenant.TenantContext;

import com.onlineStoreCom.entity.brand.Brand;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class TenantFilterIntegrationTest {

    @Autowired
    private EntityManager em;

    private Brand brand;



    @AfterEach
    public void cleanup() {
        TenantContext.clear();
    }

    @Test
    public void testTenantFilterActivated() {
        // 1️⃣ حدد الـ tenantId الحالي
        TenantContext.setTenantId(0L);

        // 2️⃣ فعّل فلتر الـ tenant على الـ session الحقيقي
        Session session = em.unwrap(Session.class);
        session.enableFilter("tenantFilter")
                .setParameter("tenantId", TenantContext.getTenantId());

        // 3️⃣ استعلام البيانات بعد الفلتر
        List<Brand> brands = em.createQuery("FROM Brand", Brand.class)
                .getResultList();

        // 4️⃣ تحقق إن كل البيانات تخص الـ tenant الحالي
        assertTrue(brands.stream().allMatch(b -> b.getTenantId().equals(0L)));

        System.out.println("Filtered brands count: " + brands.size());
        brands.forEach(b -> System.out.println(b.getName() + " -> Tenant: " + b.getTenantId()));

        // 5️⃣ مسح الـ tenantContext بعد الاختبار
        TenantContext.clear();
    }
}
