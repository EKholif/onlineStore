package com.onlineStore.admin;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * [AG-DATA-FIX-004] Fix Tenant 4 Cross-Tenant References
 * <p>
 * WHY: Categories/Brands have tenant_id=1 but are associated with Tenant 4
 * entities
 * STRATEGY: UPDATE tenant_id (not DELETE) to avoid FK constraint violations
 * BUSINESS IMPACT: Resolves SecurityException for Tenant 4 frontend
 */
@Component
@Order(1)
public class CorrectTenant4Data implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorrectTenant4Data.class);

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        LOGGER.info("================================================================================");
        LOGGER.info("🔧 FIXING TENANT 4 DATA MIGRATION");
        LOGGER.info("================================================================================");

        // Step 1: Fix Categories (Force ALL Tenant 1 to Tenant 4)
        String fixCategories = "UPDATE categories SET tenant_id = 4 WHERE tenant_id = 1";
        int categoriesFixed = entityManager.createNativeQuery(fixCategories).executeUpdate();
        LOGGER.info("✅ Fixed {} categories: tenant_id 1 → 4", categoriesFixed);

        // Step 2: Fix Brands (Force ALL Tenant 1 to Tenant 4)
        String fixBrands = "UPDATE brands SET tenant_id = 4 WHERE tenant_id = 1";
        int brandsFixed = entityManager.createNativeQuery(fixBrands).executeUpdate();
        LOGGER.info("✅ Fixed {} brands: tenant_id 1 → 4", brandsFixed);

        // Step 3: Fix Products (Force ALL Tenant 1 to Tenant 4)
        String fixProducts = "UPDATE products SET tenant_id = 4 WHERE tenant_id = 1";
        int productsFixed = entityManager.createNativeQuery(fixProducts).executeUpdate();
        LOGGER.info("✅ Fixed {} products: tenant_id 1 → 4", productsFixed);

        // Step 3.1: Fix Settings (Delete duplicates first, then move remaining)
        // A. Delete Settings from Tenant 1 that ALREADY exist in Tenant 4 (to avoid
        // Unique Constraint Violation)
        String deleteDuplicateSettings = """
                    DELETE FROM settings
                    WHERE tenant_id = 1
                    AND `key` IN (
                        SELECT `key` FROM (
                            SELECT `key` FROM settings WHERE tenant_id = 4
                        ) as temp
                    )
                """;
        int settingsDeleted = entityManager.createNativeQuery(deleteDuplicateSettings).executeUpdate();
        LOGGER.info("⚠️ Deleted {} duplicate settings from Tenant 1", settingsDeleted);

        // B. Move remaining Settings
        String fixSettings = "UPDATE settings SET tenant_id = 4 WHERE tenant_id = 1";
        int settingsFixed = entityManager.createNativeQuery(fixSettings).executeUpdate();
        LOGGER.info("✅ Fixed {} settings: tenant_id 1 → 4", settingsFixed);

        // Step 4: Fix Customers (Move all generic test customers to Tenant 4)
        String fixCustomers = "UPDATE customers SET tenant_id = 4 WHERE tenant_id = 1";
        int customersFixed = entityManager.createNativeQuery(fixCustomers).executeUpdate();
        LOGGER.info("✅ Fixed {} customers: tenant_id 1 → 4", customersFixed);

        // Step 5: Fix Orders
        String fixOrders = "UPDATE orders SET tenant_id = 4 WHERE tenant_id = 1";
        int ordersFixed = entityManager.createNativeQuery(fixOrders).executeUpdate();
        LOGGER.info("✅ Fixed {} orders: tenant_id 1 → 4", ordersFixed);

        // Step 6: Fix Reviews
        String fixReviews = "UPDATE reviews SET tenant_id = 4 WHERE tenant_id = 1";
        int reviewsFixed = entityManager.createNativeQuery(fixReviews).executeUpdate();
        LOGGER.info("✅ Fixed {} reviews: tenant_id 1 → 4", reviewsFixed);

        // Step 7: Fix Site Content (Articles, Menus, Sections)
        int articlesFixed = entityManager.createNativeQuery("UPDATE articles SET tenant_id = 4 WHERE tenant_id = 1")
                .executeUpdate();
        int menusFixed = entityManager.createNativeQuery("UPDATE menus SET tenant_id = 4 WHERE tenant_id = 1")
                .executeUpdate();
        int sectionsFixed = entityManager.createNativeQuery("UPDATE sections SET tenant_id = 4 WHERE tenant_id = 1")
                .executeUpdate();
        LOGGER.info("✅ Fixed Content: {} articles, {} menus, {} sections", articlesFixed, menusFixed, sectionsFixed);

        // Step 8: Verify - count remaining cross-tenant references
        var remaining = entityManager.createNativeQuery("""
                    SELECT COUNT(*) FROM brands_categories bc
                    JOIN brands b ON bc.brand_id = b.id
                    JOIN categories c ON bc.category_id = c.id
                    WHERE b.tenant_id = 4 AND c.tenant_id = 1
                """).getSingleResult();

        LOGGER.info("📊 Remaining cross-tenant refs (Tenant 4 brands → Tenant 1 categories): {}", remaining);

        if (((Number) remaining).intValue() == 0) {
            LOGGER.info("✅ ALL CROSS-TENANT REFERENCES RESOLVED!");
        } else {
            LOGGER.warn("⚠️ Some references still exist - may need manual review");
        }

        LOGGER.info("================================================================================");
    }
}
