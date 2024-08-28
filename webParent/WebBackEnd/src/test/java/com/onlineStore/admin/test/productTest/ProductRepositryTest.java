package com.onlineStore.admin.test.productTest;


import com.onlineStore.admin.product.repository.ProductRepository;
import com.onlineStoreCom.entity.brand.Brand;
import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepositryTest {

@Autowired
private ProductRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateProduct() {

        Category category = entityManager.find(Category.class, 15);
        Brand brand = entityManager.find(Brand.class, 40);
        Product product = new Product();

        product.setName("Samsung A31");
        product.setAlias("Samsung Galaxy A31");
        product.setPrice(456);
        product.setShortDescription( " good phone");
        product.setFullDescription( " this really good condition");
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(678);
        product.setCost(600);
        product.setEnable(true);
        product.setInStock(true);
        product.setMainImage("ehab.com");
        Product savedProduct = repository.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }


    @Test
    public void testSaveproductImages() {
        
        Product product = repository.getReferenceById(13);

        product.setMainImage("Ehab1");
        product.addExtraImages("Ehab1");
        product.addExtraImages("Ehab2");
        product.addExtraImages("Ehab3");
        product.addExtraImages("Ehab4");

        Product savedProduct = repository.save(product);
        assertThat(savedProduct.getImages().size()).isEqualTo(12);
        
    }

    @Test
    public void testSaveproductDetails() {

        Product product = repository.getReferenceById(132);

        product.addProductDetails("Processor","I5");
        product.addProductDetails("Ram","32");
        product.addProductDetails("Hard","256");
        product.addProductDetails("Ehab1","surface");
        product.addProductDetails("Ehab1","new");


        Product savedProduct = repository.save(product);
        assertThat(savedProduct.getImages().size()).isEqualTo(12);

    }

    }
