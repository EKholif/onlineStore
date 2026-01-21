package com.onlineStore.admin.product;

import com.onlineStore.admin.product.repository.ProductRepository;
import com.onlineStoreCom.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ProductListTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void listAllProducts() {
        System.out.println("============= LISTING ALL PRODUCTS =============");
        Iterable<Product> products = productRepository.findAll();
        int count = 0;
        for (Product product : products) {
            System.out.printf("Product ID: %d | Name: %s | Alias: %s | Enabled: %s%n",
                    product.getId(), product.getName(), product.getAlias(), product.isEnabled());
            count++;
        }
        System.out.println("============= TOTAL PRODUCTS: " + count + " =============");
    }
}
