package com.onlineStoreCom.entity.saas;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "business_domains")
public class BusinessDomain extends IdBasedEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 255)
    private String icon;

    @OneToMany(mappedBy = "businessDomain", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BusinessTemplate> templates = new HashSet<>();

    public BusinessDomain() {
    }

    public BusinessDomain(String name) {
        this.name = name;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Set<BusinessTemplate> getTemplates() {
        return templates;
    }

    public void setTemplates(Set<BusinessTemplate> templates) {
        this.templates = templates;
    }
}
