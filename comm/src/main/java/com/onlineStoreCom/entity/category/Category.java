package com.onlineStoreCom.entity.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "categories")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category extends IdBasedEntity implements Comparable<Category> {



    @Column(name = "name", length = 85, nullable = false, unique = true)
    private String name;

    @Column(name = "alias", length = 85, nullable = false, unique = true)
    private String alias;

    @Column(name = "image", length = 125, nullable = true)
    private String image;

    private boolean enable;


    public boolean isHasChildren() {
        return hasChildren;
    }

    @Column(name = "all_parent_ids", length = 256, nullable = true)
    private String allParentIDs;

    private int level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_Id")
    private Category parent;


    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private Set<Category> children = new HashSet<>();

    @Transient
    private boolean hasChildren;

    public Category(String name) {

        this.name = name;
    }

    public Category() {
    }

    public Category(String name, Category parent) {
        this(name);
        this.parent = parent;
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

    public Category(String name, String alias, boolean enable, Category parent) {
        this.name = name;
        this.alias = alias;
        this.enable = enable;
        this.parent = parent;

    }


    public String getAllParentIDs() {
        return allParentIDs;
    }

    public void setAllParentIDs(String allParentIDs) {
        this.allParentIDs = allParentIDs;
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
        copyCategory.setEnable(category.isEnable());
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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public Set<Category> getChildren() {
        return children;
    }

    public void setChildren(Set<Category> children) {
        this.children = children;
    }


    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    @Transient
    public String getCatImagePath() {
        String dirName = "/categories-photos/";

        if (id == null || id < 0 || image == null || image.isEmpty()) {
            return "/images/bob.png";
        }

        return dirName + this.id + '/' + this.image;
    }

    @Transient
    public String getImageDir() {
        String dirName = "categories-photos\\";
        if (id < 0 || image == null) return "\\images \\ bob.png";
        return dirName + this.id + '\\';
    }


    @Override
    public int compareTo(Category other) {
        // Implement comparison logic based on the natural ordering of Category objects
        // For example, compare by name
        return this.getName().compareTo(other.getName());
    }
}
