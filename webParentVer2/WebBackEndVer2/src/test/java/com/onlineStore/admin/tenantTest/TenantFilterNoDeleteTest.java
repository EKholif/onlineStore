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
public class TenantFilterNoDeleteTest {

    @Autowired
    private EntityManager em;

    @Test
    public void testTenantFilterWithoutDeletingData() {
        // استعلام أي براند موجود
        List<Brand> allBrands = em.createQuery("FROM Brand", Brand.class)
                .setMaxResults(5) // ناخد أول ٥ بس كعينه
                .getResultList();

        System.out.println("Before applying tenant filter:");
        allBrands.forEach(b -> System.out.println(b.getName() + " -> Tenant: " + b.getTenantId()));

        // اختار tenantId لأي براند موجود كفلتر
        if (!allBrands.isEmpty()) {
            Long tenantId = allBrands.get(0).getTenantId();
            TenantContext.setTenantId(tenantId);

            // تطبيق الفلتر
            Session session = em.unwrap(Session.class);
            session.enableFilter("tenantFilter")
                    .setParameter("tenantId", TenantContext.getTenantId());

            // استعلام بعد تفعيل الفلتر
            List<Brand> filteredBrands = em.createQuery("FROM Brand", Brand.class)
                    .getResultList();

            System.out.println("\nAfter applying tenant filter for tenant " + tenantId + ":");
            filteredBrands.forEach(b -> System.out.println(b.getName() + " -> Tenant: " + b.getTenantId()));

            // تحقق أن كل البيانات اللي رجعت تخص الـ tenant المحدد
            assertTrue(filteredBrands.stream().allMatch(b -> b.getTenantId().equals(tenantId)));

            TenantContext.clear();
        }
    }
}
