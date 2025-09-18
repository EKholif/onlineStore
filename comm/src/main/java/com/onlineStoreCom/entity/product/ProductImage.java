package com.onlineStoreCom.entity.product;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

import java.io.File;

@Entity
@Table(name = "product_images")
public class ProductImage extends IdBasedEntity {




    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductImage() {
    }


    public ProductImage(String name, Product product) {
        this.name = name;
        this.product = product;
    }

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

    @Transient
    public String getImagePath() {

        String dirName = "products-photos" + File.separator;
        if (this== null  ) return  "/images" + "\\" + "pngwing.com.png";

        return  "/products-photos/" +
        product.getId() + "/extras/" + this.name;

    }

}
