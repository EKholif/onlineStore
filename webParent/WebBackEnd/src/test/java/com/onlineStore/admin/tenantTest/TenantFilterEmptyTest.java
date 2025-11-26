package com.onlineStore.admin.tenantTest;

import com.onlineStore.admin.security.tenant.TenantContext;
import com.onlineStoreCom.entity.setting.subsetting.TenantAwareEntity;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class TenantFilterEmptyTest {

    @Autowired
    private EntityManager em;

    @Test
    public void testTenantFilterWithNonExistentTenant() {
        // حدد tenantId غير موجود
        Long tenantId = 99999L;
        TenantContext.setTenantId(tenantId);

        // تفعيل فلتر Hibernate
        Session session = em.unwrap(Session.class);
        session.enableFilter("tenantFilter")
                .setParameter("tenantId", tenantId);

        // استعلام أي جدول يرث TenantAwareEntity
        List<?> items = em.createQuery("FROM Product").getResultList();

        // المفروض مفيش أي نتائج
        Assertions.assertTrue(items.isEmpty(), "الفلتر لازم يرجع فارغ لو tenantId مش موجود!");

        System.out.println("Filtered items count for non-existent tenant: " + items.size());

        TenantContext.clear();
    }
}
