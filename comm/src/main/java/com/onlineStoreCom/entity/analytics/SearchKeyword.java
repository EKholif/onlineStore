package com.onlineStoreCom.entity.analytics;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "search_keyword",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "keyword"}))
public class SearchKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(nullable = false, length = 100)
    private String keyword;

    @Column(nullable = false)
    private Long count = 0L;

    @Column(name = "last_updated", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    public SearchKeyword() {
    }

    public SearchKeyword(Integer tenantId, String keyword) {
        this.tenantId = tenantId;
        this.keyword = keyword;
        this.lastUpdated = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void incrementCount() {
        this.count++;
        this.lastUpdated = new Date();
    }
}
