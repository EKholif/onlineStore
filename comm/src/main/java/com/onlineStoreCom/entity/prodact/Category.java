package com.onlineStoreCom.entity.prodact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "categories")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", length = 85, nullable = false, unique = true)

    private String name;

    @Column(name = "alias", length = 85, nullable = false, unique = true)

    private String alias;

    @Column(name = "image", length = 125, nullable = true)

    private String image;

    private boolean enable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_Id")
    private Category parent;


    @OneToMany(mappedBy = "parent" , fetch = FetchType.LAZY)
    private Set <Category> children = new HashSet<>();


    public Category(String name) {

        this.name = name;
    }

    public Category() {
    }

    public Category(String name, Category parent) {
        this(name);
        this.parent =parent;
    }

    public Category(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(String name, String alias, boolean enable, Category parent) {
        this.name = name;
        this.alias = alias;
        this.enable = enable;
        this.parent = parent;
    }

    public static Long ParentId (Category category){

        return category.getId();

    }
    public Category(long id) {
        this.id = id;
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
    public String StringParent() {
        return parent.getName();
    }


    public String parentName (Category parent)
    {
             return parent.getName();

    }
    public List<String> childNames(Category category) {

        Set<Category> childSet= category.getChildren();
        List<String> names = new ArrayList<>();

        for (Category child : childSet) {
            names.add(child.getName());
        }

        return names;
    }


    @Transient
    public String getCatImagePath() {
        String dirName =   "/categories-photos/" ;


        if ( id <0 || image == null) return "/images" + "\\" +"bob.png";

        return dirName + this.id + '\\' +this.image;
    }

    @Transient
    public String getImageDir() {
        String dirName =   "categories-photos\\" ;
//        if ( id == null || user_bio == null) return "\\images \\ bob.png";
        return dirName + this.id + '\\' ;
    }

    @Transient
    public String parentN(Category category) {
        return  category.getName() ;
    }


}
