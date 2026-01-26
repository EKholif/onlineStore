package com.onlineStoreCom.entity.saas;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "business_templates")
public class BusinessTemplate extends IdBasedEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(length = 255)
    private String thumbnail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_domain_id", nullable = false)
    private BusinessDomain businessDomain;

    @Lob
    @Column(name = "default_features", length = 4096)
    private String defaultFeatures; // JSON

    @Lob
    @Column(name = "default_settings", length = 4096)
    private String defaultSettings; // JSON

    @Lob
    @Column(name = "default_menu_structure", length = 4096)
    private String defaultMenuStructure; // JSON

    public BusinessTemplate() {
    }

    public BusinessTemplate(String name, BusinessDomain businessDomain) {
        this.name = name;
        this.businessDomain = businessDomain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public BusinessDomain getBusinessDomain() {
        return businessDomain;
    }

    public void setBusinessDomain(BusinessDomain businessDomain) {
        this.businessDomain = businessDomain;
    }

    public String getDefaultFeatures() {
        return defaultFeatures;
    }

    public void setDefaultFeatures(String defaultFeatures) {
        this.defaultFeatures = defaultFeatures;
    }

    public String getDefaultSettings() {
        return defaultSettings;
    }

    public void setDefaultSettings(String defaultSettings) {
        this.defaultSettings = defaultSettings;
    }

    public String getDefaultMenuStructure() {
        return defaultMenuStructure;
    }

    public void setDefaultMenuStructure(String defaultMenuStructure) {
        this.defaultMenuStructure = defaultMenuStructure;
    }
}
