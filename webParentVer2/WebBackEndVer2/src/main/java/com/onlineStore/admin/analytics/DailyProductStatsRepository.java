package com.onlineStore.admin.analytics;

import com.onlineStoreCom.entity.analytics.DailyProductStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface DailyProductStatsRepository
                extends JpaRepository<DailyProductStats, Long>, JpaSpecificationExecutor<DailyProductStats> {

        Optional<DailyProductStats> findByProductIdAndTenantIdAndDate(Integer productId, Integer tenantId, Date date);

        @Modifying
        @Query("UPDATE DailyProductStats s SET s.viewCount = s.viewCount + 1 WHERE s.id = ?1")
        void incrementViewCount(Long id);

        @Modifying
        @Query("UPDATE DailyProductStats s SET s.cartAddCount = s.cartAddCount + 1 WHERE s.id = ?1")
        void incrementCartAddCount(Long id);

        @Modifying
        @Query("UPDATE DailyProductStats s SET s.salesCount = s.salesCount + 1, s.revenue = s.revenue + ?2 WHERE s.id = ?1")
        void recordSale(Long id, Double revenue);

        // Tenant Analytics
        @Query("SELECT s.productId, SUM(s.viewCount) as totalViews FROM DailyProductStats s WHERE s.tenantId = ?1 GROUP BY s.productId ORDER BY totalViews DESC")
        org.springframework.data.domain.Page<Object[]> findTopViewedProducts(Integer tenantId,
                        org.springframework.data.domain.Pageable pageable);

        @Query("SELECT s.productId, SUM(s.salesCount) as totalSales FROM DailyProductStats s WHERE s.tenantId = ?1 GROUP BY s.productId ORDER BY totalSales DESC")
        org.springframework.data.domain.Page<Object[]> findTopSellingProducts(Integer tenantId,
                        org.springframework.data.domain.Pageable pageable);

        // Platform Analytics (Global)
        @Query("SELECT SUM(s.revenue) FROM DailyProductStats s WHERE s.date = ?1")
        Double getGlobalTotalRevenue(Date date);

        @Query("SELECT s.productId, SUM(s.viewCount) as totalViews FROM DailyProductStats s GROUP BY s.productId ORDER BY totalViews DESC")
        org.springframework.data.domain.Page<Object[]> getGlobalTopViewedProducts(
                        org.springframework.data.domain.Pageable pageable);

        @Query("SELECT COUNT(DISTINCT s.tenantId) FROM DailyProductStats s")
        Long getActiveTenantsCount();
}
