package com.onlineStore.admin.seeding;

import com.onlineStore.admin.category.CategoryRepository;
import com.onlineStore.admin.setting.country.SettingRepository;
import com.onlineStore.services.service.repository.ProductRepository;
import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.entity.product.ProductType;
import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import com.onlineStoreCom.repo.TenantRepository;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
public class TenantZeroSeeder implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantZeroSeeder.class);

    @Autowired
    private TenantRepository tenantRepo;
    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private SettingRepository settingRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Run only if we are in a context where we want to seed Tenant 0
        LOGGER.info("[AG-SEED-001] Starting Tenant Zero (Company Site) Seeding...");

        // Ensure we are operating as Tenant Zero
        TenantContext.setTenantId(0L);

        // Manually enable Filter for Background/CLI Context to ensure Strict
        // Multi-Tenancy
        // This prevents "IncorrectResultSizeDataAccessException" when duplicates exist
        // across tenants
        Session session = entityManager.unwrap(Session.class);
        org.hibernate.Filter filter = session.enableFilter("tenantFilter");
        filter.setParameter("tenantId", 0L);
        filter.validate();

        try {
            // 1. Seed Categories (Navigation)
            seedCategory("Solutions", "solutions");
            seedCategory("Investors", "investors");
            seedCategory("Resources", "resources");

            // 2. Seed Products (Subscription Plans)
            // Startup Tier: $29/mo, 20 Products, 5 Users
            seedPlan("Startup Tier", "startup-tier", 29.00f, "Standard", "10GB");

            // Enterprise Core: Call for price, Unlimited
            seedPlan("Enterprise Core", "enterprise-core", 0.00f, "Unlimited", "Unlimited");

            // 3. Seed Theme Settings (Dark Mode)
            // Force specific colors for Tenant 0 to differentiate it (Dogfooding Visuals)
            seedThemeSetting("THEME_COLOR_PRIMARY", "#0a192f"); // Deep Navy
            seedThemeSetting("THEME_COLOR_SECONDARY", "#64ffda"); // Cyber Cyan
            seedThemeSetting("THEME_FONT_HEADING", "'Space Grotesk', sans-serif");

        } catch (Exception e) {
            LOGGER.error("[AG-SEED-001] Error during Tenant Zero seeding", e);
        } finally {
            // Always clear context to avoid polluting following runners
            TenantContext.clear();
        }

        LOGGER.info("[AG-SEED-001] Tenant Zero Seeding process finished.");
    }

    private void seedCategory(String name, String alias) {
        Category existing = categoryRepo.findByAlias(alias);
        if (existing == null) {
            Category c = new Category(name);
            c.setAlias(alias);
            c.setEnabled(true);
            c.setTenantId(0L);
            // Note: TenantListener should override this with Context ID, but setting it
            // explicitly is safe.
            c.setImage("default.png"); // FIX for DB constraint (NOT NULL)

            categoryRepo.save(c);
            LOGGER.info("[AG-SEED-001] Seeded Category: {}", name);
        } else {
            LOGGER.debug("[AG-SEED-001] Category already exists: {}", name);
        }
    }

    private void seedPlan(String name, String alias, float price, String tenantLimit, String storageLimit) {
        Product existing = productRepo.findByAlias(alias);
        if (existing == null) {
            Product p = new Product(name);
            p.setAlias(alias);
            p.setPrice(price);
            p.setShortDescription("Platform Subscription Plan");
            p.setFullDescription("<h1>" + name + "</h1><p>Experience the power of our platform.</p>");
            p.setEnabled(true);
            p.setInStock(true);
            p.setMainImage("server-icon.svg"); // Placeholder asset
            p.setCreatedTime(new Date());
            p.setUpdatedTime(new Date());
            p.setProductType(ProductType.SUBSCRIPTION);

            // Feature Flags for Plan Objects
            p.setHasShipping(false);
            p.setTrackStock(false);

            // Add Details (Plan Features)
            // Note: passing 0L as tenantId for details explicitly, aligning with Product
            p.addProductDetails("Tenants", tenantLimit, 0L);
            p.addProductDetails("Storage", storageLimit, 0L);

            productRepo.save(p);
            LOGGER.info("[AG-SEED-001] Seeded Plan: {}", name);
        } else {
            LOGGER.debug("[AG-SEED-001] Plan already exists: {}", name);
        }
    }

    private void seedThemeSetting(String key, String value) {
        // findByKey might return a Global setting or Tenant 0 setting.
        // We want to ensure specific Tenant 0 setting exists.
        // With Filter ENABLED, this should strictly return tenant 0 or global (with
        // null check in code if needed)

        Setting existing = settingRepo.findByKey(key);

        if (existing == null) {
            Setting s = new Setting(key, value, SettingCategory.THEME);
            settingRepo.save(s);
            LOGGER.info("[AG-SEED-001] Create Setting: {}", key);
        } else {
            // Implementation Decision:
            // If it exists, and we are Tenant 0, we Update it to enforce the "Company Site"
            // look.
            // If we didn't, user wouldn't see the Dark Mode they paid for (metaphorically).
            if (value != null && !value.equals(existing.getValue())) {
                existing.setValue(value);
                settingRepo.save(existing);
                LOGGER.info("[AG-SEED-001] Enforce Setting: {}", key);
            }
        }
    }
}
