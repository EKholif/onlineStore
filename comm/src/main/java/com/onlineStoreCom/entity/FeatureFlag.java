package com.onlineStoreCom.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "feature_flags", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "feature_key", "tenant_id" })
})
public class FeatureFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "feature_key", nullable = false)
    private String key;

    @Column(nullable = false)
    private boolean isEnabled;

    @Column(name = "tenant_id")
    private String tenantId; // Null for global default

    private String description;

    public FeatureFlag() {
    }

    public FeatureFlag(String key, boolean isEnabled, String tenantId, String description) {
        this.key = key;
        this.isEnabled = isEnabled;
        this.tenantId = tenantId;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
