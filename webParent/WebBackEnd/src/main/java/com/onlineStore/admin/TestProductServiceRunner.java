




package com.onlineStore.admin;

import com.onlineStore.admin.product.service.ProductService;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class TestProductServiceRunner implements CommandLineRunner {

    @Autowired private ProductService productService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==================================================");
        System.out.println("TESTING PRODUCT ASSOCIATIONS PAGE 5");
        System.out.println("==================================================");

        try {
            TenantContext.setTenantId(4L);
            System.out.println("Set Tenant Context to 4");

            int pageNum = 5;
            Page<Product> page = productService.listByPage(pageNum, "name", "asc", null);
            
            for (Product p : page.getContent()) {
                System.out.println("Checking Product ID: " + p.getId());
                
                // Check Brand
                if (p.getBrand() != null) {
                    System.out.println(" - Brand: " + p.getBrand().getName());
                } else {
                    System.err.println("!!! BRAND IS NULL for Product " + p.getId());
                }

                // Check Category
                if (p.getCategory() != null) {
                    System.out.println(" - Category: " + p.getCategory().getName());
                } else {
                    System.err.println("!!! CATEGORY IS NULL for Product " + p.getId());
                }
            }

        } catch (Exception e) {
            System.err.println("CRITICAL FAILURE:");
            e.printStackTrace();
        }
        System.out.println("==================================================");
    }
}
