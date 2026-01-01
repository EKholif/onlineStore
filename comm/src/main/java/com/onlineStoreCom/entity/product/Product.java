package com.onlineStoreCom.entity.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onlineStoreCom.entity.brand.Brand;
import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;

import java.io.File;
import java.util.*;

@Entity
@Table(name = "products")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Product extends IdBasedEntity {

    @Column(unique = true, length = 256, nullable = false)
    private String name;

    @Column(unique = true, length = 256, nullable = false)
    private String alias;

    @Column(length = 512, nullable = false, name = "short_description")
    private String shortDescription;

    @Column(length = 4096, nullable = false, name = "full_description")
    private String fullDescription;

    @Column(name = "created_time", nullable = false, updatable = false)
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;
    @Column(name = "enabled")
    private boolean enable;

    @Column(name = "in_stock")
    private boolean inStock;

    private float cost;

    private float price;

    @Column(name = "discount_percent")
    private float discountPercent;

    private float length;
    private float width;
    private float height;
    private float weight;

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private final List<ProductDetails> details = new ArrayList<>();

    private int reviewCount;
    private float averageRating;

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public boolean isCustomerCanReview() {
        return customerCanReview;
    }

    public void setCustomerCanReview(boolean customerCanReview) {
        this.customerCanReview = customerCanReview;
    }

    public boolean isReviewedByCustomer() {
        return reviewedByCustomer;
    }

    public void setReviewedByCustomer(boolean reviewedByCustomer) {
        this.reviewedByCustomer = reviewedByCustomer;
    }

    @Transient
    private boolean customerCanReview;
    @Transient
    private boolean reviewedByCustomer;

    public Product(Integer id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public Product(Integer id) {
        this.id = id;
    }

    public Product(String name) {
        this.name = name;
    }

    public List<ProductDetails> getDetails() {

        return details;
    }

    public void addProductDetails(Integer id, String detailName, String detailValue, Long tenantId) {
        this.details.add(new ProductDetails(id, detailName, detailValue, tenantId, this));
    }

    public void addProductDetails(String detailName, String detailValue, Long tenantId) {
        this.details.add(new ProductDetails(detailName, detailValue, tenantId, this));
    }

    public void addProductDetailsTenantId(Long tenantId) {
        this.details.add(new ProductDetails(tenantId, this));
    }

    public void addExtraImages(String imageName) {

        this.images.add(new ProductImage(imageName, this));

    }

    public Set<ProductImage> getImages() {
        return images;
    }

    public void setImages(Set<ProductImage> images) {
        this.images = images;
    }

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    public Product() {
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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
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

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    @Transient
    public float getDiscountPrice() {
        if (discountPercent > 0) {
            return price * ((100 - discountPercent) / 100);
        }
        return this.price;
    }

    @Transient
    public String getImagePath() {
        String dirName = "/products-photos/";

        if (id == null || mainImage == null)
            return "/images" + "\\" + "pngwing.com.png";

        return dirName + this.id + '\\' + this.mainImage;
    }

    @Transient
    public String getExtraImagesPath() {
        String dirName = "/products-photos/";

        if (id == null || mainImage == null)
            return "/images" + "\\" + "pngwing.com.png";

        return dirName + this.id + '\\' + this.images;
    }

    @Transient
    public String getImageDir() {
        String dirName = "products-photos\\";
        if (id == -1L || mainImage == null)
            return "\\images \\ bob.png";
        return dirName + this.id + '\\';
    }

    @Transient
    public String getExtraImageDir() {
        String dirName = "products-photos" + File.separator;
        if (id == -1L || mainImage == null)
            return "\\images \\ bob.png";
        return dirName + this.id + File.separator + "extras";
    }

    @Transient
    public String getShortName() {
        if (name.length() > 70) {
            return name.substring(0, 70).concat("...");
        }
        return name;
    }

    @Transient
    public String getURI() {
        return "/p/" + this.alias;
    }

}
