package com.onlineStoreCom.entity.analytics;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "daily_product_stats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"date", "product_id", "tenant_id"}))
public class DailyProductStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "cart_add_count", nullable = false)
    private Long cartAddCount = 0L;

    @Column(name = "sales_count", nullable = false)
    private Long salesCount = 0L;

    @Column(name = "revenue", nullable = false)
    private Double revenue = 0.0;

    public DailyProductStats() {
    }

    public DailyProductStats(Date date, Integer productId, Integer tenantId) {
        this.date = date;
        this.productId = productId;
        this.tenantId = tenantId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getCartAddCount() {
        return cartAddCount;
    }

    public void setCartAddCount(Long cartAddCount) {
        this.cartAddCount = cartAddCount;
    }

    public Long getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Long salesCount) {
        this.salesCount = salesCount;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    // Increment methods for convenience and atomic-like operations logic
    public void incrementViews() {
        this.viewCount++;
    }

    public void incrementCartAdds() {
        this.cartAddCount++;
    }

    public void recordSale(Double amount) {
        this.salesCount++;
        this.revenue += amount;
    }
}
