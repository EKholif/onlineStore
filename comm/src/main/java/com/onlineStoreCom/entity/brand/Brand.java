package com.onlineStoreCom.entity.brand;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brands")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Brand extends IdBasedEntity {

    @Column(nullable = false, length = 45, unique = true)
    private String name;

    @Column(nullable = false, length = 128)
    private String logo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "brands_categories", joinColumns = @JoinColumn(name = "brand_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    public Brand() {
    }

    public Brand(Integer id, String name) {
        this.name = name;
        this.id = id;
    }

    public Brand(String name) {
        this.name = name;
        this.logo = "brand-logo.png";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Transient
    public String getImagePath() {
        String dirName = "/brands-photos/";

        if (id == null || id < 0 || logo == null)
            return "/images/bob.png";

        return dirName + this.getTenantId() + "/" + this.id + "/" + this.logo;
    }

    @Transient
    public String getImageDir() {
        String dirName = "brands-photos/";
        if (id == -1L || logo == null)
            return "/images/bob.png";
        return dirName + this.getTenantId() + "/" + this.id + "/";
    }

    @Override
    public String toString() {
        return "Brand [id=" + id + ", name=" + name + ", categories=" + categories + "]";
    }
}
