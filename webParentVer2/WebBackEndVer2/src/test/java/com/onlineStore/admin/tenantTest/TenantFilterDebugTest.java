package com.onlineStore.admin.tenantTest;

import com.onlineStoreCom.entity.brand.Brand;
import com.onlineStoreCom.tenant.TenantContext;
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
public class TenantFilterDebugTest {

    @Autowired
    private EntityManager em;

    @Test
    public void testQueryUsesFilter() {
        // نحدد tenantId معين
        TenantContext.setTenantId(8247009068765685744L);

        // نأخذ session من نفس EM
        Session session = em.unwrap(Session.class);

        // تفعيل الفلتر على الـ session ده
        session.enableFilter("tenantFilter").setParameter("tenantId", TenantContext.getTenantId());
        System.out.println("🔥 Tenant filter enabled on EM: " + TenantContext.getTenantId());

        // استعلام JPQL
        List<Brand> brands = em.createQuery("FROM Brand", Brand.class).getResultList();

        // تحقق من كل النتائج تخص الـ tenant الصح
        assertTrue(brands.stream().allMatch(b -> b.getTenantId().equals(1L)));

        // اطبع النتائج مع حالة الفلتر
        brands.forEach(b -> System.out.println(b.getName() + " -> Tenant: " + b.getTenantId()));

        TenantContext.clear();
    }
}
