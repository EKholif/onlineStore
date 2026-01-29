package com.onlineStore.admin.brand;

import com.onlineStore.admin.brand.reposetry.BrandRepository;
import com.onlineStoreCom.entity.brand.Brand;
import com.onlineStoreCom.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(true)
public class BrandRepositoryTest {

    @Autowired
    private BrandRepository repo;

    @BeforeEach
    public void setup() {
        TenantContext.setTenantId(101L);
    }

    @AfterEach
    public void tearDown() {
        TenantContext.clear();
    }

    @Test
    public void testSaveBrand_WithContext_ShouldSucceed() {
        Brand brand = new Brand("TestBrand_101");
        Brand saved = repo.save(brand);

        assertThat(saved.getId()).isGreaterThan(0);
        assertThat(saved.getTenantId()).isEqualTo(101L);
    }

    @Test
    public void testSaveBrand_NoContext_ShouldFail() {
        TenantContext.clear();
        Brand brand = new Brand("FailBrand");

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            repo.save(brand);
        });
        assertThat(exception.getMessage()).contains("No Tenant Context");
    }

    @Test
    public void testSaveBrand_CrossTenant_ShouldFail() {
        Brand brand = new Brand("StolenBrand");
        brand.setTenantId(999L); // Malicious attempt to save for another tenant

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            repo.save(brand);
        });
        assertThat(exception.getMessage()).contains("Cross-Tenant Write Attempt");
    }
}
