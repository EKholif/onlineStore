package com.onlineStore.admin.saas.service;

import com.onlineStore.admin.saas.repository.BusinessTemplateRepository;
import com.onlineStore.admin.saas.repository.TenantTemplateProductRepository;
import com.onlineStore.admin.setting.country.SettingRepository;
import com.onlineStore.services.service.repository.ProductRepository;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.entity.saas.BusinessTemplate;
import com.onlineStoreCom.entity.saas.TenantTemplateProduct;
import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.entity.tenant.TenantStatus;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class TenantProvisioningService {

    @Autowired
    private BusinessTemplateRepository templateRepository;

    @Autowired
    private TenantTemplateProductRepository demoProductRepo;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SettingRepository settingRepository;

    // We need a repository for Tenant, assuming one exists or using generic logic
    // Injecting Generic Respository or Specific one if created
    // For now assuming we persist Tenant via EntityManager or a TenantRepository if
    // available
    // Let's use EntityManager to avoid circular deps with existing Tenant services
    // if any
    @PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    @Transactional
    public Tenant provisionTenant(Integer templateId, String tenantName, String tenantCode, String adminEmail) {
        // 1. Fetch Template
        BusinessTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));

        // 2. Create Tenant
        Tenant newTenant = new Tenant();
        newTenant.setName(tenantName);
        newTenant.setCode(tenantCode);
        newTenant.setStatus(TenantStatus.ACTIVE);
        newTenant.setCreatedAt(new Date());
        newTenant.setTemplate(template);
        newTenant.setOwnerTenantId(TenantContext.getTenantId()); // Current tenant is the reseller/owner

        entityManager.persist(newTenant);
        entityManager.flush(); // To generate ID

        // 3. Clone Demo Products
        cloneDemoProducts(template, newTenant.getId());

        // 4. Copy Default Settings (Theme, Features)
        applyTemplateSettings(template, newTenant.getId());

        return newTenant;
    }

    private void cloneDemoProducts(BusinessTemplate template, Long targetTenantId) {
        List<TenantTemplateProduct> demos = demoProductRepo.findByTemplate(template);

        for (TenantTemplateProduct demo : demos) {
            Product p = new Product();
            p.setName(demo.getName());
            p.setAlias(demo.getAlias() + "-" + System.currentTimeMillis()); // Ensure unique alias
            p.setShortDescription(demo.getShortDescription());
            p.setFullDescription(demo.getFullDescription());
            p.setMainImage(demo.getMainImage());
            p.setCreatedTime(new Date());
            p.setUpdatedTime(new Date());
            p.setProductType(demo.getProductType());
            p.setPrice(demo.getPrice());
            p.setCost(demo.getCost());
            p.setEnabled(demo.isEnabled());
            p.setInStock(demo.isInStock());

            // Flags
            p.setTrackStock(demo.isTrackStock());
            p.setHasShipping(demo.isHasShipping());
            p.setHasScheduling(demo.isHasScheduling());

            // Set Tenant
            p.setTenantId(targetTenantId);

            // Save
            productRepository.save(p);
        }
    }

    private void applyTemplateSettings(BusinessTemplate template, Long targetTenantId) {
        // Parse JSON settings from template (simplified for now)
        // In a real impl, we would use Jackson to parse template.getDefaultSettings()
        // Here we just set a default theme as an example of provisioning

        Setting themeColorQuery = new Setting("THEME_COLOR", "#007bff", SettingCategory.THEME); // Default Blue
        themeColorQuery.setTenantId(targetTenantId);
        settingRepository.save(themeColorQuery);

        // Add more based on template logic...
    }
}
