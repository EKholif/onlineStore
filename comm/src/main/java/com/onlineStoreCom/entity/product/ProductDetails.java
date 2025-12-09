package com.onlineStoreCom.entity.product;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

/**
 * Represents a detailed attribute of a product.
 * <p>
 * Each ProductDetails is linked to a {@link Product} and has a name and value.
 * Example: name="Color", value="Red".
 * </p>
 */
@Entity
@Table(name = "product_details")
public class ProductDetails extends IdBasedEntity {

    /** Name of the detail (e.g., "Color", "Size"). */
    @Column(nullable = false, length = 255)
    private String name;

    /** Value of the detail (e.g., "Red", "XL"). */
    @Column(nullable = false, length = 255)
    private String value;

    /** The product associated with this detail. */
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // ---------------- Constructors ----------------

    public ProductDetails() { }

    public ProductDetails(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public ProductDetails(Long tenantId, Product product) {
        this.setTenantId(tenantId);
        this.product = product;
    }

    public ProductDetails(String detailName, String detailValue, Long tenantId, Product product) {
        this.name = detailName;
        this.value = detailValue;
        this.product = product;
        this.setTenantId(tenantId);
    }
    public ProductDetails( int id,String detailName, String detailValue, Product product) {
        this.name = detailName;
        this.value = detailValue;
        this.product = product;
        this.id=id;
    }

    public ProductDetails(int id, String detailName, String detailValue, Long tenantId, Product product) {
        this.name = detailName;
        this.value = detailValue;
        this.product = product;
        this.setTenantId(tenantId);
        this.id = id;
    }
    public ProductDetails(String name, String value, Product product) {
        this.name = name;
        this.value = value;
        this.product = product;
    }



    // ---------------- Getters & Setters ----------------

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
