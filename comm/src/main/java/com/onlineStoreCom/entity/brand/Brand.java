package com.onlineStoreCom.entity.brand;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onlineStoreCom.entity.category.Category;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brands")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, length = 45, unique = true)
    private String name;

    @Column(nullable = false, length = 128)
    private String logo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "brands_categories",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    public Brand() {
    }

    public Brand(long id, String name, String logo) {
        this.id = id;
        this.name = name;
        this.logo = logo;

    }

    public Brand(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Brand(String name) {
        this.name = name;
        this.logo = "brand-logo.png";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

//    @Transient
//    public String getLogoPath() {
//        if (this.id == null) return "/images/image-thumbnail.png";
//
//        return Constants.S3_BASE_URI + "/brands-photos/" + this.id + "/" + this.logo;
//    }


    @Transient
    public String getImagePath() {
        String dirName = "/brands-photos/";


        if (id < 0 || logo == null) return "/images" + "\\" + "bob.png";

        return dirName + this.id + '\\' + this.logo;
    }


    @Transient
    public String getImageDir() {
        String dirName = "brands-photos\\";
        if (id == -1L || logo == null) return "\\images \\ bob.png";
        return dirName + this.id + '\\';
    }

    @Override
    public String toString() {
        return "Brand [id=" + id + ", name=" + name + ", categories=" + categories + "]";
    }
}

