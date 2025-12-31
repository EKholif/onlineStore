package com.onlineStore.admin.tenantTest;


import com.onlineStoreCom.entity.brand.Brand;
import com.onlineStoreCom.entity.category.Category;
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
public class TenantFilterCheckTest {

    @Autowired
    private EntityManager em;

    @Test
    public void testFilterAppliedOnSameEntityManager() {

        // 1️⃣ Set tenant id
        TenantContext.setTenantId(8247009068765685744L);

        // 2️⃣ Enable filter on the real EntityManager
        Session session = em.unwrap(Session.class);
        session.enableFilter("tenantFilter")
                .setParameter("tenantId", TenantContext.getTenantId());

        System.out.println("🔥 Tenant filter enabled for tenantId: " + TenantContext.getTenantId());

        // 3️⃣ Run query using the same EntityManager
        List<Brand> brands = em.createQuery("FROM Brand", Brand.class).getResultList();
        List<Category> Category = em.createQuery("FROM Category", Category.class).getResultList();

        System.out.println("Filtered brands count: " + brands.size());
        brands.forEach(b -> System.out.println(b.getName() + " -> Tenant: " + b.getTenantId()));
        System.out.println("Filtered brands count: " + Category.size());
        Category.forEach(b -> System.out.println(b.getName() + " -> Tenant: " + b.getTenantId()));
        session.getSessionFactory().getStatistics().setStatisticsEnabled(true);
        System.out.println(session.getSessionFactory().getStatistics().getQueryExecutionCount());

        // 4️⃣ Assert all results belong to tenant 1
        assertTrue(brands.stream().allMatch(b -> b.getTenantId().equals(0L)),
                "Some brands don't belong to the current tenant!");

        // ✅ Cleanup
        TenantContext.clear();
    }
}
