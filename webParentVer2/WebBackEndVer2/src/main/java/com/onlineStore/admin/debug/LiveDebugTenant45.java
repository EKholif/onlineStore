package com.onlineStore.admin.debug;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * [AG-DEBUG-TENANT45-001] Live Debug: Tenant 4 accessing Tenant 5 data
 * <p>
 * WHY: SecurityException shows Tenant 4 trying to access Tenant 5 data
 * GOAL: Find all cross-tenant references between Tenant 4 and 5
 */
@Component
@Order(1)
public class LiveDebugTenant45 implements CommandLineRunner {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) throws Exception {
        System.out.println("=".repeat(80));
        System.out.println("🔍 LIVE DEBUG: Tenant 4 → Tenant 5 Cross-References");
        System.out.println("=".repeat(80));

        // Check categories
        System.out.println("\n📦 CATEGORIES:");
        var catCount4 = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM categories WHERE tenant_id = 4").getSingleResult();
        var catCount5 = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM categories WHERE tenant_id = 5").getSingleResult();
        System.out.println("  Tenant 4 categories: " + catCount4);
        System.out.println("  Tenant 5 categories: " + catCount5);

        // Check brands
        System.out.println("\n🏷️ BRANDS:");
        var brandCount4 = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM brands WHERE tenant_id = 4").getSingleResult();
        var brandCount5 = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM brands WHERE tenant_id = 5").getSingleResult();
        System.out.println("  Tenant 4 brands: " + brandCount4);
        System.out.println("  Tenant 5 brands: " + brandCount5);

        // Check products
        System.out.println("\n🛍️ PRODUCTS:");
        var prodCount4 = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM products WHERE tenant_id = 4").getSingleResult();
        var prodCount5 = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM products WHERE tenant_id = 5").getSingleResult();
        System.out.println("  Tenant 4 products: " + prodCount4);
        System.out.println("  Tenant 5 products: " + prodCount5);

        // CRITICAL: Find cross-tenant references
        System.out.println("\n🔗 CROSS-TENANT REFERENCES (4 → 5):");

        // Categories with parent from different tenant
        var crossParentCat = entityManager.createNativeQuery("""
                    SELECT c.id, c.name, c.tenant_id, p.id, p.name, p.tenant_id
                    FROM categories c
                    JOIN categories p ON c.parent_id = p.id
                    WHERE c.tenant_id = 4 AND p.tenant_id = 5
                """).getResultList();
        System.out.println("  Categories (Tenant 4) with parent (Tenant 5): " + crossParentCat.size());
        for (Object row : crossParentCat) {
            Object[] cols = (Object[]) row;
            System.out.println(
                    "    → Category #" + cols[0] + " (" + cols[1] + ") parent is #" + cols[3] + " (" + cols[4] + ")");
        }

        // Brands with categories from different tenant
        var crossBrandCat = entityManager.createNativeQuery("""
                    SELECT bc.brand_id, b.name, b.tenant_id, bc.category_id, c.name, c.tenant_id
                    FROM brands_categories bc
                    JOIN brands b ON bc.brand_id = b.id
                    JOIN categories c ON bc.category_id = c.id
                    WHERE b.tenant_id = 4 AND c.tenant_id = 5
                """).getResultList();
        System.out.println("  Brands (Tenant 4) linked to categories (Tenant 5): " + crossBrandCat.size());
        for (Object row : crossBrandCat) {
            Object[] cols = (Object[]) row;
            System.out.println(
                    "    → Brand #" + cols[0] + " (" + cols[1] + ") uses Category #" + cols[3] + " (" + cols[4] + ")");
        }

        // Products with category from different tenant
        var crossProdCat = entityManager.createNativeQuery("""
                    SELECT p.id, p.name, p.tenant_id, p.category_id, c.name, c.tenant_id
                    FROM products p
                    JOIN categories c ON p.category_id = c.id
                    WHERE p.tenant_id = 4 AND c.tenant_id = 5
                """).getResultList();
        System.out.println("  Products (Tenant 4) with category (Tenant 5): " + crossProdCat.size());
        for (Object row : crossProdCat) {
            Object[] cols = (Object[]) row;
            System.out.println("    → Product #" + cols[0] + " (" + cols[1] + ") uses Category #" + cols[3] + " ("
                    + cols[4] + ")");
        }

        // Products with brand from different tenant
        var crossProdBrand = entityManager.createNativeQuery("""
                    SELECT p.id, p.name, p.tenant_id, p.brand_id, b.name, b.tenant_id
                    FROM products p
                    JOIN brands b ON p.brand_id = b.id
                    WHERE p.tenant_id = 4 AND b.tenant_id = 5
                """).getResultList();
        System.out.println("  Products (Tenant 4) with brand (Tenant 5): " + crossProdBrand.size());
        for (Object row : crossProdBrand) {
            Object[] cols = (Object[]) row;
            System.out.println(
                    "    → Product #" + cols[0] + " (" + cols[1] + ") uses Brand #" + cols[3] + " (" + cols[4] + ")");
        }

        // Check reverse direction (5 → 4)
        System.out.println("\n🔗 CROSS-TENANT REFERENCES (5 → 4):");
        var crossBrandCatReverse = entityManager.createNativeQuery("""
                    SELECT bc.brand_id, b.name, b.tenant_id, bc.category_id, c.name, c.tenant_id
                    FROM brands_categories bc
                    JOIN brands b ON bc.brand_id = b.id
                    JOIN categories c ON bc.category_id = c.id
                    WHERE b.tenant_id = 5 AND c.tenant_id = 4
                """).getResultList();
        System.out.println("  Brands (Tenant 5) linked to categories (Tenant 4): " + crossBrandCatReverse.size());

        System.out.println("\n" + "=".repeat(80));
        System.out.println("✅ DEBUG COMPLETE");
        System.out.println("=".repeat(80));
    }
}
