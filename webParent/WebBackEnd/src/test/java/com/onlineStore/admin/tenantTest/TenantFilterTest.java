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
@Transactional  // كل التغييرات هتترجع rollback بعد كل اختبار
public class TenantFilterTest {

    @Autowired
    private EntityManager em;

    @Test
    public void testTenantFilterOnExistingData() {
        // حدد الـ tenantId اللي عايز تختبره
        Long tenantId = 8247009068765685744L;
        TenantContext.setTenantId(tenantId);

        // فعل فلتر Hibernate
        Session session = em.unwrap(Session.class);
        session.enableFilter("tenantFilter")
                .setParameter("tenantId", tenantId);

        // استعلم أي جدول عنده TenantAwareEntity
        // هنا افترضنا جدول المنتجات مثلا Product
        List<?> items = em.createQuery("FROM Product").getResultList();

        // تأكد إن كل البيانات اللي رجعت ليها tenantId = tenantId الحالي
        boolean allMatch = items.stream().allMatch(entity -> {
            if (entity instanceof TenantAwareEntity) {
                return ((TenantAwareEntity) entity).getTenantId().equals(tenantId);
            }
            return false;
        });

        Assertions.assertTrue(allMatch, "بعض البيانات مش للـ tenantId الحالي!");

        System.out.println("Filtered items count: " + items.size());
        items.forEach(item -> {
            TenantAwareEntity tEntity = (TenantAwareEntity) item;
            System.out.println("TenantId: " + tEntity.getTenantId() + " | " + tEntity);
        });

        // نظف السياق بعد الاختبار
        TenantContext.clear();
    }
}
