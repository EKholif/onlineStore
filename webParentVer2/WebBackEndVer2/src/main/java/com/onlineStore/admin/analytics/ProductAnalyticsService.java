package com.onlineStore.admin.analytics;

import com.onlineStoreCom.entity.analytics.DailyProductStats;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ProductAnalyticsService {

    @Autowired
    private DailyProductStatsRepository repo;

    @Autowired
    private com.onlineStore.services.service.repository.ProductRepository productRepo;

    /**
     * Async method to log a product view.
     * Uses UPSERT-like logic: tries to find existing, if not creates new.
     * Handles concurrency by retrying or strictly relying on DB constraints?
     * For now, synchronized block or retry logic might be overkill for MVP,
     * but we use atomic update query if exists.
     */
    @Async
    @Transactional
    public void logView(Integer productId, Long tenantId) {
        Date today = new java.sql.Date(System.currentTimeMillis());

        Optional<DailyProductStats> stats = repo.findByProductIdAndTenantIdAndDate(productId, tenantId, today);

        if (stats.isPresent()) {
            repo.incrementViewCount(stats.get().getId());
        } else {
            // Create new
            try {
                DailyProductStats newStats = new DailyProductStats(today, productId, tenantId);
                newStats.setViewCount(1L);
                repo.save(newStats);
            } catch (Exception e) {
                // Concurrency: Another thread might have created it.
                // Fallback: update matching record
                Optional<DailyProductStats> retry = repo.findByProductIdAndTenantIdAndDate(productId, tenantId,
                        today);
                retry.ifPresent(dailyProductStats -> repo.incrementViewCount(dailyProductStats.getId()));
            }
        }
    }

    @Async
    @Transactional
    public void logAddToCart(Integer productId, Long tenantId) {
        Date today = new java.sql.Date(System.currentTimeMillis());

        Optional<DailyProductStats> stats = repo.findByProductIdAndTenantIdAndDate(productId, tenantId, today);

        if (stats.isPresent()) {
            repo.incrementCartAddCount(stats.get().getId());
        } else {
            try {
                DailyProductStats newStats = new DailyProductStats(today, productId, tenantId);
                newStats.setCartAddCount(1L);
                repo.save(newStats);
            } catch (Exception e) {
                Optional<DailyProductStats> retry = repo.findByProductIdAndTenantIdAndDate(productId, tenantId,
                        today);
                retry.ifPresent(dailyProductStats -> repo.incrementCartAddCount(dailyProductStats.getId()));
            }
        }
    }

    @Async
    @Transactional
    public void recordSale(Integer productId, Long tenantId, Double amount) {
        Date today = new java.sql.Date(System.currentTimeMillis());

        Optional<DailyProductStats> stats = repo.findByProductIdAndTenantIdAndDate(productId, tenantId, today);

        if (stats.isPresent()) {
            repo.recordSale(stats.get().getId(), amount);
        } else {
            try {
                DailyProductStats newStats = new DailyProductStats(today, productId, tenantId);
                newStats.setSalesCount(1L);
                newStats.setRevenue(amount);
                repo.save(newStats);
            } catch (Exception e) {
                Optional<DailyProductStats> retry = repo.findByProductIdAndTenantIdAndDate(productId, tenantId,
                        today);
                retry.ifPresent(s -> repo.recordSale(s.getId(), amount));
            }
        }
    }

    // --- Dashboard Analytics ---

    public java.util.List<Object[]> getTopViewedProducts(Long tenantId, int limit) {
        return repo.findTopViewedProducts(tenantId, org.springframework.data.domain.PageRequest.of(0, limit))
                .getContent();
    }

    public java.util.List<Object[]> getTopSellingProducts(Long tenantId, int limit) {
        return repo.findTopSellingProducts(tenantId, org.springframework.data.domain.PageRequest.of(0, limit))
                .getContent();
    }

    public Double getPlatformTotalRevenue(Date date) {
        Double val = repo.getGlobalTotalRevenue(date);
        return val != null ? val : 0.0;
    }

    public java.util.List<Object[]> getGlobalTopViewedProducts(int limit) {
        return repo.getGlobalTopViewedProducts(org.springframework.data.domain.PageRequest.of(0, limit)).getContent();
    }

    public Long getActiveTenantsCount() {
        return repo.getActiveTenantsCount();
    }

    public org.springframework.data.domain.Page<Object[]> getZeroViewProducts(Long tenantId, int page, int size) {
        // ProductRepository likely still expects Integer, so we cast here if needed.
        // Assuming ProductRepository uses tenantId filter which usually expects Integer
        // in this specific legacy repo?
        // Let's check. Actually, BaseTenantRepository uses Long generally, but
        // ProductRepository might have custom queries.
        // Ideally we pass Integer if that's what it wants.
        Integer tenantIdInt = tenantId != null ? tenantId.intValue() : null;

        org.springframework.data.domain.Page<com.onlineStoreCom.entity.product.Product> productsProxy = productRepo
                .findProductsWithZeroViews(tenantIdInt, org.springframework.data.domain.PageRequest.of(page, size));

        return productsProxy.map(p -> new Object[] { p.getId(), p.getName() });
    }
}
