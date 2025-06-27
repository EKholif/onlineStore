package com.onlineStore.admin.brand;


import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.category.controller.CategoryController;
import com.onlineStore.admin.category.services.CategoryDTO;
import com.onlineStoreCom.entity.brand.Brand;
import com.onlineStoreCom.entity.category.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class BrandRestController {

    @Autowired
    private BrandService service;


    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @PostMapping("/check_unique_name")
    public String checkDuplicateName(@Param("id") Long id, @Param("name") String name, @Param("alies") String alies) {

        logger.info("Checking uniqueness for id={}, name={}, alies={}", id, name, alies);


        String result = service.checkUnique(id, name);

        logger.info("Result: {}", result);
        return result;
    }


    @GetMapping("/list-categories/{id}/category")
    public List<CategoryDTO> listCategories(@PathVariable(name = "id") Long id) throws CategoryNotFoundException, BrandNotFoundException {

        List<CategoryDTO> listCategories = new ArrayList<>();

        Brand brand = service.findById(id);


        Set<Category> set = brand.getCategories();

        for (Category cat : set) {
            CategoryDTO categoryDTO = new CategoryDTO(cat.getId(), cat.getName());
            listCategories.add(categoryDTO);
        }


        return listCategories;


    }


}


