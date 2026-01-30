package com.onlineStore.admin.seeding;

import com.onlineStore.admin.category.CategoryRepository;
import com.onlineStore.admin.setting.country.SettingRepository;
import com.onlineStore.services.service.repository.ProductRepository;
import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class TenantZeroSeederTest {

    @Autowired
    private TenantZeroSeeder seeder;
    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private SettingRepository settingRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    public void testTenantZeroSeeding() throws Exception {
        // Run Seeder
        // Pass empty args
        seeder.run();

        // Verification Context: Switch to 0 to see the data
        TenantContext.setTenantId(0L);

        // Enable Hibernate Filter for strict multi-tenancy verification
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("tenantFilter").setParameter("tenantId", 0L);

        try {
            // 1. Verify Category
            Category c = categoryRepo.findByAlias("investors");
            assertThat(c).isNotNull();
            assertThat(c.getName()).isEqualTo("Investors");

            // 2. Verify Product (Plan)
            Product p = productRepo.findByAlias("startup-tier");
            assertThat(p).isNotNull();
            assertThat(p.getPrice()).isEqualTo(29.00f);
            assertThat(p.getProductType().name()).isEqualTo("SUBSCRIPTION");

            // Check Details
            assertThat(p.getDetails()).isNotEmpty();
            boolean hasTenants = p.getDetails().stream()
                    .anyMatch(d -> d.getName().equals("Tenants"));
            assertThat(hasTenants).isTrue();

            // 3. Verify Theme
            // With Filter enabled, this should return unique result
            Setting s = settingRepo.findByKey("THEME_COLOR_PRIMARY");
            assertThat(s).isNotNull();
            assertThat(s.getValue()).isEqualTo("#0a192f");

        } finally {
            TenantContext.clear();
        }
    }
}
