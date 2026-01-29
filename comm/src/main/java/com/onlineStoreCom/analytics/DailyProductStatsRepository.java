package com.onlineStoreCom.analytics;

import com.onlineStoreCom.entity.analytics.DailyProductStats;
import com.onlineStoreCom.repo.base.BaseTenantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyProductStatsRepository extends BaseTenantRepository<DailyProductStats, Long> {

    @Query("SELECT s FROM DailyProductStats s WHERE s.productId = ?1 AND s.date = ?2")
    Optional<DailyProductStats> findByProductAndDate(Integer productId, Date date);

    @Query("SELECT SUM(s.viewCount) FROM DailyProductStats s WHERE s.productId = ?1")
    Long getLifetimeViews(Integer productId);

    // --- Tenant Level Analytics ---

    @Query("SELECT s.productId, SUM(s.viewCount) as totalViews FROM DailyProductStats s WHERE s.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()} GROUP BY s.productId ORDER BY totalViews DESC")
    Page<Object[]> findTopViewedProducts(Pageable pageable);

    @Query("SELECT s.productId, SUM(s.salesCount) as totalSales, SUM(s.revenue) as totalRevenue FROM DailyProductStats s WHERE s.tenantId = :#{T(com.onlineStoreCom.tenant.TenantContext).getTenantId()} GROUP BY s.productId ORDER BY totalSales DESC")
    Page<Object[]> findTopSellingProducts(Pageable pageable);

    @Query(value = "SELECT p.id, p.name FROM products p " +
            "LEFT JOIN daily_product_stats s ON p.id = s.product_id AND s.tenant_id = :tenantId " +
            "WHERE s.product_id IS NULL AND p.tenant_id = :tenantId", countQuery = "SELECT count(*) FROM products p " +
            "LEFT JOIN daily_product_stats s ON p.id = s.product_id AND s.tenant_id = :tenantId " +
            "WHERE s.product_id IS NULL AND p.tenant_id = :tenantId", nativeQuery = true)
    Page<Object[]> findZeroViewProducts(@Param("tenantId") Integer tenantId, Pageable pageable);

    // --- RootMaster / Platform Analytics ---

    @Query(value = "SELECT SUM(view_count) FROM daily_product_stats", nativeQuery = true)
    Long getGlobalTotalViews();

    @Query(value = "SELECT product_id, SUM(view_count) as total_views FROM daily_product_stats GROUP BY product_id ORDER BY total_views DESC", nativeQuery = true)
    List<Object[]> getGlobalTopViewedProducts(Pageable pageable);

    @Query("SELECT COUNT(DISTINCT s.tenantId) FROM DailyProductStats s")
    Long getActiveTenantsCount();

    @Query(value = "SELECT SUM(revenue) FROM daily_product_stats", nativeQuery = true)
    Double getGlobalTotalRevenue();

    // --- Legacy Support for AnalyticsService ---
    @Query("SELECT s FROM DailyProductStats s WHERE s.tenantId = ?1 AND s.productId = ?2 AND s.date = ?3")
    Optional<DailyProductStats> findByTenantIdAndProductIdAndDate(Integer tenantId, Integer productId, Date date);

    @Query("SELECT SUM(s.revenue) FROM DailyProductStats s WHERE s.tenantId = ?1 AND s.date = ?2")
    Double sumRevenueForDate(Integer tenantId, Date date);

    @Query("SELECT SUM(s.revenue) FROM DailyProductStats s WHERE s.date = ?1")
    Double sumPlatformRevenueForDate(Date date);

    @Query("SELECT s.productId, SUM(s.viewCount) as totalViews FROM DailyProductStats s WHERE s.tenantId = ?1 GROUP BY s.productId ORDER BY totalViews DESC")
    List<Object[]> findTopViewed(Integer tenantId, Pageable pageable);

    @Query("SELECT s.productId, SUM(s.salesCount) as totalSales, SUM(s.revenue) as totalRevenue FROM DailyProductStats s WHERE s.tenantId = ?1 GROUP BY s.productId ORDER BY totalSales DESC")
    List<Object[]> findTopSelling(Integer tenantId, Pageable pageable);
}
