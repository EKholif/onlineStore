package com.onlineStore.admin.product;


import com.onlineStore.admin.product.service.ProductService;
import com.onlineStore.admin.category.controller.CategoryController;
import com.onlineStoreCom.entity.product.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProdactRestController {

    @Autowired
    private ProductService service;


    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);


    @PostMapping("/check_unique_product")
    public String checkDuplicateName(@Param("id") Integer id, @Param("name") String name, @Param("alias") String alias) {

        logger.info("Checking uniqueness for id={}, name={}, alias={}", id, name, alias);


        String result = service.checkUnique(id, name, alias);


        logger.info("Result: {}", result);
        return result;
    }
    @GetMapping("/products/get/{id}")
    public ProductDTO getProductInfo(@PathVariable("id") Integer id)
            throws ProductNotFoundException {
        Product product = service.findById(id);
        return new ProductDTO(product.getName(), product.getImagePath(),
                product.getDiscountPrice(), product.getCost());
    }

//    @GetMapping("/list-categories/{id}/category")
//    public List<CategoryDTO> listCategories(@PathVariable(name ="id") Integer id) throws CategoryNotFoundException, BrandNotFoundException {
//
//      List<CategoryDTO> listCategories = new ArrayList<>();
//        System.out.println(id);
//
//        Product brand = service.findById(id);
//
//
//        Set<Category> set = brand.getCategories();
//
//        for (Category cat : set) {
//            CategoryDTO categoryDTO = new CategoryDTO(cat.getId(), cat.getName());
//            listCategories.add(categoryDTO);
//        }
//        System.out.println(listCategories);


//        return listCategories;
//
//
//    }


}


