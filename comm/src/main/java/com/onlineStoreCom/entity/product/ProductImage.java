package com.onlineStoreCom.entity.product;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

/**
 * Represents an image associated with a product.
 * <p>
 * Each ProductImage is linked to a {@link Product} and has a name representing
 * the filename of the image.
 * </p>
 */
@Entity
@Table(name = "product_images")
public class ProductImage extends IdBasedEntity {

    /** The filename of the image. */
    @Column(nullable = false)
    private String name;

    /** The product this image belongs to. */
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // ---------------- Constructors ----------------

    public ProductImage() {
    }

    public ProductImage(String name, Product product) {
        this.name = name;
        this.product = product;
    }

    // ---------------- Getters & Setters ----------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // ---------------- Utility Methods ----------------

    /**
     * Returns the path to the image file to be used in the frontend.
     * <p>
     * If the image object is null, returns a default placeholder image path.
     * </p>
     *
     * @return the relative path to the image
     */
    @Transient
    public String getImagePath() {
        if (this.product == null || this.product.getId() == null)
            return "/images/pngwing.com.png";

        // AG-ASSET-PATH-006: Entity-First Protocol
        // Returns: /tenants/{tenantId}/{productId}/assets/products/extras/{filename}
        Long effectiveTenantId = this.product.getTenantId();
        if (effectiveTenantId == null)
            effectiveTenantId = 0L;

        return "/tenants/" + effectiveTenantId + "/" + this.product.getId()
                + "/assets/products/extras/" + this.name;
    }
}
