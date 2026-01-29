package com.onlineStoreCom.entity.saas;

import com.onlineStoreCom.entity.product.ProductType;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tenant_template_products")
public class TenantTemplateProduct extends IdBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private BusinessTemplate template;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String alias;

    @Column(length = 1000)
    private String shortDescription;

    @Column(nullable = false)
    private String mainImage;

    @Lob
    @Column(length = 4096)
    private String fullDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    private float price;
    private float cost;
    private boolean enabled = true;
    private boolean inStock = true;

    // Feature Flags
    private boolean trackStock = true;
    private boolean hasShipping = true;
    private boolean hasScheduling = false;


    public TenantTemplateProduct() {
    }

    public BusinessTemplate getTemplate() {
        return template;
    }

    public void setTemplate(BusinessTemplate template) {
        this.template = template;
    }

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

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public boolean isTrackStock() {
        return trackStock;
    }

    public void setTrackStock(boolean trackStock) {
        this.trackStock = trackStock;
    }

    public boolean isHasShipping() {
        return hasShipping;
    }

    public void setHasShipping(boolean hasShipping) {
        this.hasShipping = hasShipping;
    }

    public boolean isHasScheduling() {
        return hasScheduling;
    }

    public void setHasScheduling(boolean hasScheduling) {
        this.hasScheduling = hasScheduling;
    }
}
