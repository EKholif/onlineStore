package com.onlineStore.admin.category.services;

public class CategoryDTO {

    public Integer id;
    public String name;

    public CategoryDTO() {
    }

    public CategoryDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
