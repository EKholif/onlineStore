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
public class TenantFilterMultipleTest {

    @Autowired
    private EntityManager em;

    @Test
    public void testTenantFilterForMultipleTenants() {
        // tenant 1
        TenantContext.setTenantId(1L);
        Session session1 = em.unwrap(Session.class);
        session1.enableFilter("tenantFilter").setParameter("tenantId", 1L);
        List<?> tenant1Items = em.createQuery("FROM Product").getResultList();
        System.out.println("Tenant 1 items count: " + tenant1Items.size());
        Assertions.assertTrue(tenant1Items.stream().allMatch(i -> ((TenantAwareEntity) i).getTenantId().equals(1L)));

        TenantContext.clear();

        // tenant 2
        TenantContext.setTenantId(2L);
        Session session2 = em.unwrap(Session.class);
        session2.enableFilter("tenantFilter").setParameter("tenantId", 2L);
        List<?> tenant2Items = em.createQuery("FROM Product").getResultList();
        System.out.println("Tenant 2 items count: " + tenant2Items.size());
        Assertions.assertTrue(tenant2Items.stream().allMatch(i -> ((TenantAwareEntity) i).getTenantId().equals(2L)));

        TenantContext.clear();
    }

    @Test
    public void testTenantFilterBeforeLogin() {
        // Tenant غير محدد
        TenantContext.clear();

        Session session = em.unwrap(Session.class);
        session.enableFilter("tenantFilter").setParameter("tenantId", 0L); // 0 أو master tenant

        List<?> items = em.createQuery("FROM Product").getResultList();

        System.out.println("Items count without tenant: " + items.size());

        // التأكد إن البيانات ترجع فارغ أو فقط master tenant
        Assertions.assertTrue(items.stream().allMatch(i -> ((TenantAwareEntity) i).getTenantId().equals(0L)));
    }
}
