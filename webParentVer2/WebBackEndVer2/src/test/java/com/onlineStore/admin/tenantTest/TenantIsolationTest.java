package com.onlineStore.admin.tenantTest;

import com.onlineStore.admin.brand.reposetry.BrandRepository;
import com.onlineStore.admin.category.CategoryRepository;
import com.onlineStoreCom.entity.brand.Brand;
import com.onlineStoreCom.tenant.TenantContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class TenantIsolationTest {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setup() {
        // Clear Context
        TenantContext.clear();
    }

    @Test
    public void testBrandUniquenessPerTenant() {
        // Tenant 1
        TenantContext.setTenantId(1L);
        Brand b1 = new Brand("Nike");
        brandRepository.save(b1); // Should succeed

        // Tenant 2
        TenantContext.setTenantId(2L);
        Brand b2 = new Brand("Nike"); // Same name, different tenant
        brandRepository.save(b2); // Should succeed (No Global Unique Constraint)

        // Verify isolation
        Assertions.assertNotNull(b1.getId());
        Assertions.assertNotNull(b2.getId());
        Assertions.assertNotEquals(b1.getId(), b2.getId());

        System.out.println("✅ Success: Saved 'Nike' for Tenant 1 and Tenant 2.");
    }

    @Test
    public void testBrandDuplicateInSameTenantFails() {
        TenantContext.setTenantId(3L);
        Brand b1 = new Brand("Adidas");
        brandRepository.save(b1);

        Brand b2 = new Brand("Adidas");

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            brandRepository.save(b2);
        });

        System.out.println("✅ Success: Prevented duplicate 'Adidas' in Tenant 3.");
    }
}
