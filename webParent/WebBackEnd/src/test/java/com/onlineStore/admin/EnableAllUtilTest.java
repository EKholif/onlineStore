package com.onlineStore.admin;

import com.onlineStore.admin.category.CategoryRepository;
import com.onlineStore.admin.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Rollback(false)
public class EnableAllUtilTest {

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private ProductRepository productRepo;

    @Test
    @jakarta.transaction.Transactional
    public void enableAll() {
        System.out.println("Running Enable All...");
        // Set Tenant Context (assuming 1 is main tenant or system)
        com.onlineStoreCom.tenant.TenantContext.setTenantId(1L);

        // Enable All Categories
        categoryRepo.enableCategoryAll();

        // Enable All Products
        // [AG-TEN-RISK-001] Secure Bulk Update
        productRepo.enableProductAll();

        System.out.println("All Products and Categories Enabled!");
    }
}
