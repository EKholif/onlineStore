package com.onlineStore.admin.category;


import com.onlineStore.admin.category.controller.CategoryController;
import com.onlineStore.admin.category.services.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryRestController {

    @Autowired
    private CategoryService service;


    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @PostMapping("/check_unique")
    public String checkDuplicateEmail(@Param("id") Long id, @Param("name") String name, @Param("alies") String alies) {

        logger.info("Checking uniqueness for id={}, name={}, alies={}", id, name, alies);


        String result = service.checkUnique(id, name, alies);

        logger.info("Result: {}", result);
        return result;
    }


}


