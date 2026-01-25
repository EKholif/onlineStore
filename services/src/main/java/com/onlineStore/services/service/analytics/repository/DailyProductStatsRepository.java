package com.onlineStore.services.service.analytics.repository;

import com.onlineStoreCom.entity.analytics.DailyProductStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface DailyProductStatsRepository extends JpaRepository<DailyProductStats, Long> {
    Optional<DailyProductStats> findByTenantIdAndProductIdAndDate(Integer tenantId, Integer productId, Date date);

    @org.springframework.data.jpa.repository.Query("SELECT p.productId, SUM(p.viewCount) as totalViews FROM DailyProductStats p WHERE p.tenantId = ?1 GROUP BY p.productId ORDER BY totalViews DESC")
    java.util.List<Object[]> findTopViewed(Integer tenantId, org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT p.productId, SUM(p.salesCount) as totalSales, SUM(p.revenue) as totalRevenue FROM DailyProductStats p WHERE p.tenantId = ?1 GROUP BY p.productId ORDER BY totalSales DESC")
    java.util.List<Object[]> findTopSelling(Integer tenantId, org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.viewCount) FROM DailyProductStats p WHERE p.tenantId = ?1 AND p.date = ?2")
    Long sumViewsForDate(Integer tenantId, Date date);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.revenue) FROM DailyProductStats p WHERE p.tenantId = ?1 AND p.date = ?2")
    Double sumRevenueForDate(Integer tenantId, Date date);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.revenue) FROM DailyProductStats p WHERE p.date = ?1")
    Double sumPlatformRevenueForDate(Date date);
}
