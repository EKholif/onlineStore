package com.onlineStore.admin.saas.service;

import com.onlineStore.services.service.repository.ProductRepository;
import com.onlineStore.admin.saas.repository.BusinessDomainRepository;
import com.onlineStore.admin.saas.repository.BusinessTemplateRepository;
import com.onlineStore.admin.saas.repository.TenantTemplateProductRepository;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.entity.product.ProductType;
import com.onlineStoreCom.entity.saas.BusinessDomain;
import com.onlineStoreCom.entity.saas.BusinessTemplate;
import com.onlineStoreCom.entity.saas.TenantTemplateProduct;
import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.tenant.TenantContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(true) // Helper test, rollback changes
public class TenantProvisioningServiceTest {

    @Autowired
    private TenantProvisioningService provisioningService;

    @Autowired
    private BusinessDomainRepository domainRepo;

    @Autowired
    private BusinessTemplateRepository templateRepo;

    @Autowired
    private TenantTemplateProductRepository demoProductRepo;

    @Autowired
    private ProductRepository productRepo;

    @Test
    @Transactional
    public void testProvisionTenantFromTemplate() {
        // 0. Setup Context (Simulate Reseller)
        TenantContext.setTenantId(1L);

        // 1. Setup Template Data
        BusinessDomain domain = new BusinessDomain("TEST_DOMAIN_PROV");
        domainRepo.save(domain);

        BusinessTemplate template = new BusinessTemplate("CLINIC_V1", domain);
        templateRepo.save(template);

        TenantTemplateProduct demoProd = new TenantTemplateProduct();
        demoProd.setTemplate(template);
        demoProd.setName("Demo Dental Checkup");
        demoProd.setAlias("demo-dental");
        demoProd.setProductType(ProductType.BOOKING);
        demoProd.setPrice(100.0f);
        demoProductRepo.save(demoProd);

        // 2. Execute Provisioning
        Tenant newTenant = provisioningService.provisionTenant(
                template.getId(),
                "My New Clinic",
                "clinic_tenant_001",
                "admin@clinic.com");

        // 3. Verify Tenant Created
        assertThat(newTenant.getId()).isNotNull();
        assertThat(newTenant.getName()).isEqualTo("My New Clinic");
        assertThat(newTenant.getTemplate().getId()).isEqualTo(template.getId());
        assertThat(newTenant.getOwnerTenantId()).isEqualTo(1L); // Owned by Reseller 1

        // 4. Verify Products Cloned
        // We need to bypass tenant filter or set context to new tenant to see products
        // For test verification, we can query repository raw or switch context
        // But Repository uses AOP filter.
        // Let's switch context to new tenant
        TenantContext.setTenantId(newTenant.getId());

        // This findAll should only return products for new tenant
        // Note: productRepo.findAll() might be paged, check signature
        // Assuming we rely on database state.

        // Use a direct query or standard find if possible.
        // Since we are inside @Transactional, changes are visible.
        // Just checking count might be enough if filter works.

        // However, the test context is still adhering to AOP.
        // Let's try to fetch by Name checking it exists.
        Product clonedProduct = productRepo.findByName("Demo Dental Checkup");
        assertThat(clonedProduct).isNotNull();
        assertThat(clonedProduct.getProductType()).isEqualTo(ProductType.BOOKING);
        assertThat(clonedProduct.getTenantId()).isEqualTo(newTenant.getId());

        // 5. Cleanup (handled by Rollback)
    }
}
