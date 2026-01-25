package com.onlineStoreCom.entity.audit;

import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.entity.users.User;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

/**
 * AG-AUDIT-001: Audit Log Entity for Multi-Tenant Security Events
 * <p>
 * Purpose:
 * - Track all tenant context switches
 * - Log impersonation attempts (authorized and unauthorized)
 * - Record access denied events for security analysis
 * - Provide audit trail for compliance (GDPR, SOC2)
 * <p>
 * Business Impact:
 * - Security monitoring and incident response
 * - Compliance reporting
 * - User activity tracking
 * - Forensic analysis of security breaches
 */
@Entity
@Table(name = "tenant_audit_log", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_action", columnList = "action")
})
public class TenantAuditLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who performed the action
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Tenant context at the time of action
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /**
     * Action type: SWITCH_TENANT, IMPERSONATE, ACCESS_DENIED, etc.
     */
    @Column(nullable = false, length = 100)
    private String action;

    /**
     * Target tenant (for switch/impersonate operations)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_tenant_id")
    private Tenant targetTenant;

    /**
     * Client IP address (handles X-Forwarded-For)
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * User-Agent string from request
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * Timestamp of the event
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    /**
     * Additional context as JSON
     * Example: {"reason":"UNAUTHORIZED_TENANT_ACCESS","http_method":"GET"}
     */
    @Column(columnDefinition = "TEXT")
    private String details;

    // Constructors

    public TenantAuditLog() {
        this.createdAt = new Date();
    }

    public TenantAuditLog(User user, Tenant tenant, String action) {
        this();
        this.user = user;
        this.tenant = tenant;
        this.action = action;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Tenant getTargetTenant() {
        return targetTenant;
    }

    public void setTargetTenant(Tenant targetTenant) {
        this.targetTenant = targetTenant;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "TenantAuditLog{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", user=" + (user != null ? user.getId() : null) +
                ", tenant=" + (tenant != null ? tenant.getId() : null) +
                ", targetTenant=" + (targetTenant != null ? targetTenant.getId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
}
