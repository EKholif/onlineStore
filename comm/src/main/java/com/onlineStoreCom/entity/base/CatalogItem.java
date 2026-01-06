package com.onlineStoreCom.entity.base;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import java.util.Date;

@MappedSuperclass
public abstract class CatalogItem extends IdBasedEntity {

    @Column(unique = true, length = 256, nullable = false)
    protected String name;

    @Column(unique = true, length = 256, nullable = false)
    protected String alias;

    @Column(length = 512, nullable = false, name = "short_description")
    protected String shortDescription;

    @Column(length = 4096, nullable = false, name = "full_description")
    protected String fullDescription;

    @Column(name = "created_time", nullable = false, updatable = false)
    protected Date createdTime;

    @Column(name = "updated_time")
    protected Date updatedTime;

    @Column(name = "enabled")
    protected boolean enabled;

    @Column(name = "main_image", nullable = false)
    protected String mainImage;

    protected float cost;

    protected float price;

    @Column(name = "discount_percent")
    protected float discountPercent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(float discountPercent) {
        this.discountPercent = discountPercent;
    }

    @Transient
    public float getDiscountPrice() {
        if (discountPercent > 0) {
            return price * ((100 - discountPercent) / 100);
        }
        return this.price;
    }

    @Transient
    public String getShortName() {
        if (name != null && name.length() > 70) {
            return name.substring(0, 70).concat("...");
        }
        return name;
    }

    // Abstract methods or common helpers can go here
}
