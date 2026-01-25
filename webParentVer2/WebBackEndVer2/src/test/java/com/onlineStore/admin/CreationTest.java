package com.onlineStore.admin;

import com.onlineStore.admin.brand.reposetry.BrandRepository;
import com.onlineStore.admin.usersAndCustomers.users.UserRepository;
import com.onlineStoreCom.entity.brand.Brand;
import com.onlineStoreCom.tenant.TenantContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Rollback(false)
public class CreationTest {

    @Autowired
    private BrandRepository brandRepo;
    @Autowired
    private UserRepository userRepo;

    @Test
    public void testCreateBrandWithTenant() {
        // 1. Simulate Login / Context
        Long tenantId = 1L; // Assuming Tenant 1 exists or Master
        TenantContext.setTenantId(tenantId);
        System.out.println("Set Tenant Context to: " + tenantId);

        // 2. Create Brand
        Brand brand = new Brand("TestBrand_" + System.currentTimeMillis());
        // setTenantId should happen automatically via EntityListener

        try {
            Brand saved = brandRepo.save(brand);
            System.out.println("✅ Saved Brand: " + saved.getId() + " | Tenant: " + saved.getTenantId());
            assertThat(saved.getId()).isGreaterThan(0);
            assertThat(saved.getTenantId()).isEqualTo(tenantId);
        } catch (Exception e) {
            System.err.println("❌ Failed to save Brand:");
            e.printStackTrace();
            throw e;
        }
    }
}
