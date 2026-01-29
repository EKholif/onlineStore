package com.onlineStore.services.service.analytics;

import com.onlineStore.services.service.analytics.repository.SearchKeywordRepository;
import com.onlineStoreCom.entity.analytics.SearchKeyword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

@Service
@Transactional
public class AnalyticsService {

    // @Autowired
    // private DailyProductStatsRepository statsRepo;

    @Autowired
    private SearchKeywordRepository searchRepo;
    @Autowired
    private com.onlineStoreCom.repo.TenantRepository tenantRepo;

    public void recordProductView(Integer tenantId, Integer productId) {
        // Date today = new Date();
        // Date todayZeroTime = getStartOfDay();

        // DailyProductStats stats =
        // statsRepo.findByTenantIdAndProductIdAndDate(tenantId, productId,
        // todayZeroTime)
        // .orElse(new DailyProductStats(todayZeroTime, productId, tenantId));

        // stats.incrementViews();
        // statsRepo.save(stats);
    }

    public void recordSearch(Integer tenantId, String keyword) {
        if (keyword == null || keyword.isBlank()) return;
        String normalized = keyword.trim().toLowerCase();

        // SearchKeyword searchKeyword = searchRepo.findByTenantIdAndKeyword(tenantId,
        // normalized)
        // .orElse(new SearchKeyword(tenantId, normalized));

        // searchKeyword.incrementCount();
        // searchRepo.save(searchKeyword);
    }

    public void recordOrder(Integer tenantId, Map<Integer, Double> productSales) {
        // Date todayZeroTime = getStartOfDay();

        // productSales.forEach((productId, amount) -> {
        // DailyProductStats stats =
        // statsRepo.findByTenantIdAndProductIdAndDate(tenantId, productId,
        // todayZeroTime)
        // .orElse(new DailyProductStats(todayZeroTime, productId, tenantId));
        // stats.recordSale(amount);
        // statsRepo.save(stats);
        // });
    }

    public java.util.List<Object[]> getTopViewedProducts(Integer tenantId, int limit) {
        // return statsRepo.findTopViewed(tenantId,
        // org.springframework.data.domain.PageRequest.of(0, limit));
        return java.util.Collections.emptyList();
    }

    public java.util.List<Object[]> getTopSellingProducts(Integer tenantId, int limit) {
        // return statsRepo.findTopSelling(tenantId,
        // org.springframework.data.domain.PageRequest.of(0, limit));
        return java.util.Collections.emptyList();
    }

    public java.util.List<SearchKeyword> getTopSearchKeywords(Integer tenantId, int limit) {
        // return searchRepo.findByTenantIdOrderByCountDesc(tenantId,
        // org.springframework.data.domain.PageRequest.of(0, limit));
        return java.util.Collections.emptyList();
    }

    public Double getDailyRevenue(Integer tenantId, Date date) {
        // Double revenue = statsRepo.sumRevenueForDate(tenantId, date);
        // return revenue != null ? revenue : 0.0;
        return 0.0;
    }

    private Date getStartOfDay() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // Platform Level Analytics
    public Double getPlatformTotalRevenue(Date date) {
        // Double revenue = statsRepo.sumPlatformRevenueForDate(date);
        // return revenue != null ? revenue : 0.0;
        return 0.0;
    }

    public java.util.List<SearchKeyword> getPlatformTopSearchKeywords(int limit) {
        // return
        // searchRepo.findPlatformTopKeywords(org.springframework.data.domain.PageRequest.of(0,
        // limit));
        return java.util.Collections.emptyList();
    }

    public long getPlatformActiveTenantsCount() {
        return tenantRepo.count() - 1; // Exclude RootMaster (ID 0) if it exists as a tenant record, usually approximation is fine
    }
}
