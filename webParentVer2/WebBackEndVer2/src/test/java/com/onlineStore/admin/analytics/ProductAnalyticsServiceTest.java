package com.onlineStore.admin.analytics;

import com.onlineStoreCom.entity.analytics.DailyProductStats;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration; // Keep this
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ProductAnalyticsService.class)
public class ProductAnalyticsServiceTest {

    @Configuration
    @EnableJpaRepositories(basePackages = { "com.onlineStore.admin.analytics",
            "com.onlineStore.services.service.repository" })
    @EntityScan(basePackages = { "com.onlineStoreCom.entity" })
    static class TestConfig {
    }

    @Autowired
    private ProductAnalyticsService service;
    @Autowired
    private DailyProductStatsRepository repo;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @Test
    @Transactional
    public void testLogViewAndSale() throws InterruptedException {
        Integer productId = 999;
        Long tenantId = 55L; // Dummy Tenant
        Date today = new java.sql.Date(System.currentTimeMillis());

        // Cleanup before test (Optional, but good for isolation if rollback=false)
        Optional<DailyProductStats> existing = repo.findByProductIdAndTenantIdAndDate(productId, tenantId.intValue(),
                today);
        existing.ifPresent(repo::delete);

        // Ensure changes are flushed
        repo.flush();

        // 1. Log View
        service.logView(productId, tenantId);

        // Wait up to 2 seconds for Async
        long end = System.currentTimeMillis() + 2000;
        DailyProductStats stats = null;
        while (System.currentTimeMillis() < end) {
            // Need to clear cache to see async updates from other threads/transactions
            entityManager.clear();

            Optional<DailyProductStats> opt = repo.findByProductIdAndTenantIdAndDate(productId, tenantId.intValue(),
                    today);
            if (opt.isPresent()) {
                stats = opt.get();
                break;
            }
            Thread.sleep(100);
        }

        assertThat(stats).isNotNull();
        assertThat(stats.getViewCount()).isEqualTo(1L);

        // 2. Log Another View (Increment)
        service.logView(productId, tenantId);
        Thread.sleep(500); // Wait for async

        // Refresh - clear cache again
        entityManager.clear();
        stats = repo.findById(stats.getId()).get();
        assertThat(stats.getViewCount()).isEqualTo(2L);

        // 3. Record Sale
        Double saleAmount = 50.0;
        service.recordSale(productId, tenantId, saleAmount);
        Thread.sleep(500);

        entityManager.clear();
        stats = repo.findById(stats.getId()).get();
        assertThat(stats.getSalesCount()).isEqualTo(1L);
        assertThat(stats.getRevenue()).isEqualTo(50.0);

        // Cleanup
        repo.delete(stats);
    }
}
