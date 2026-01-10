package com.onlineStoreCom.entity.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onlineStoreCom.entity.setting.subsetting.HierarchicalEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Category extends HierarchicalEntity<Category> implements Comparable<Category> {

    @Column(name = "name", length = 85, nullable = false, unique = true)
    private String name;

    @Column(name = "alias", length = 85, nullable = false, unique = true)
    private String alias;

    @Column(name = "image", length = 125, nullable = true)
    private String image;

    @Column(name = "enabled")
    private boolean enabled;

    // Level is specific to Category for display formatting? Or general?
    // Keeping it here for now unless Role uses it too. Role didn't seem to have
    // 'level'.
    private int level;

    public Category(String name) {
        this.name = name;
    }

    public Category() {
    }

    public Category(String name, Category parent) {
        this(name);
        setParent(parent);
    }

    public Category(Integer id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public Category(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(String name, String alias, boolean enabled, Category parent) {
        this.name = name;
        this.alias = alias;
        this.enabled = enabled;
        setParent(parent);
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static Category copyIdAndName(Integer id, String name) {
        Category copyCategory = new Category();
        copyCategory.setId(id);
        copyCategory.setName(name);

        return copyCategory;
    }

    public static Category copyFull(Category category) {
        Category copyCategory = new Category();
        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());
        copyCategory.setImage(category.getImage());
        copyCategory.setAlias(category.getAlias());
        copyCategory.setEnabled(category.isEnabled());
        copyCategory.setParent(category.getParent());
        copyCategory.setChildren(category.getChildren());
        copyCategory.setHasChildren(category.getChildren().size() > 0);

        return copyCategory;
    }

    public static Category copyFull(Category Category, String name) {
        Category copycategory = copyFull(Category);
        copycategory.setName(name);
        return copycategory;
    }

    public Category(Integer id) {
        this.id = id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Transient
    public String getCatImagePath() {
        String dirName = "/categories-photos/";

        if (id == null || id < 0 || image == null || image.isEmpty()) {
            return "/images/bob.png";
        }

        return dirName + this.getTenantId() + "/" + this.id + '/' + this.image;
    }

    @Transient
    public String getImageDir() {
        String dirName = "categories-photos/";
        if (id < 0 || image == null)
            return "/images/bob.png";
        return dirName + this.getTenantId() + "/" + this.id + "/";
    }

    @Override
    public int compareTo(Category other) {
        return this.getName().compareTo(other.getName());
    }
}
