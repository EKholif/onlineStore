package com.onlineStoreCom.entity.tracking;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import com.onlineStoreCom.tenant.TenantAware;
import com.onlineStoreCom.tenant.TenantListener;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;

import java.util.Date;

@Entity
@Table(name = "visitor_sessions")
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@EntityListeners(TenantListener.class)
public class VisitorSession extends IdBasedEntity implements TenantAware {

    @Column(name = "tenant_id", updatable = false)
    private Long tenantId;

    @Column(name = "session_id", length = 128, nullable = false, unique = true)
    private String sessionId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "device_type", length = 20)
    private String deviceType; // Mobile, Desktop, Tablet

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "last_active_time")
    private Date lastActiveTime;

    @Column(name = "total_duration_display")
    private String totalDurationDisplay; // For easy display if needed, or calculated dynamically

    @Column(name = "page_views")
    private int pageViews = 0;

    @Column(name = "customer_id")
    private Integer customerId; // Nullable, if they log in

    public VisitorSession() {
        this.startTime = new Date();
        this.lastActiveTime = new Date();
    }

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(Date lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public String getTotalDurationDisplay() {
        return totalDurationDisplay;
    }

    public void setTotalDurationDisplay(String totalDurationDisplay) {
        this.totalDurationDisplay = totalDurationDisplay;
    }

    public int getPageViews() {
        return pageViews;
    }

    public void setPageViews(int pageViews) {
        this.pageViews = pageViews;
    }

    public void incrementPageViews() {
        this.pageViews++;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    @Transient
    public long getDurationInSeconds() {
        if (lastActiveTime == null || startTime == null)
            return 0;
        return (lastActiveTime.getTime() - startTime.getTime()) / 1000;
    }
}
