package com.onlineStore.admin.debug;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * [AG-FIX-CROSS-TENANT-001] Auto-Fix Cross-Tenant Data References
 * <p>
 * WHY: Data Guard blocks access when a child entity references a parent from a
 * different tenant.
 * GOAL: Break these links (set parent_id = null) to restore service
 * availability (Stop 500 Errors).
 */
@Component
@Order(10) // Run after diagnostics
public class FixCrossTenantData implements CommandLineRunner {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("=".repeat(80));
        System.out.println("🛠️ AUTO-FIX: CROSS-TENANT DATA PURGE");
        System.out.println("=".repeat(80));

        // 1. Fix Categories with Cross-Tenant Parents
        List<Object[]> crossParents = entityManager.createNativeQuery("""
                    SELECT c.id, c.tenant_id, p.id, p.tenant_id
                    FROM categories c
                    JOIN categories p ON c.parent_id = p.id
                    WHERE c.tenant_id != p.tenant_id
                """).getResultList();

        if (!crossParents.isEmpty()) {
            System.out.println("Found " + crossParents.size() + " categories with cross-tenant parents. Unlinking...");
            int fixedCats = entityManager.createNativeQuery("""
                        UPDATE categories c
                        JOIN categories p ON c.parent_id = p.id
                        SET c.parent_id = NULL
                        WHERE c.tenant_id != p.tenant_id
                    """).executeUpdate();
            System.out.println("✅ Fixed " + fixedCats + " categories (parent_id set to NULL).");
        } else {
            System.out.println("✅ No cross-tenant category parents found.");
        }

        // 2. Fix Products with Cross-Tenant Categories
        List<Object[]> crossProdCats = entityManager.createNativeQuery("""
                    SELECT p.id, p.tenant_id, c.id, c.tenant_id
                    FROM products p
                    JOIN categories c ON p.category_id = c.id
                    WHERE p.tenant_id != c.tenant_id
                """).getResultList();

        if (!crossProdCats.isEmpty()) {
            System.out
                    .println("Found " + crossProdCats.size() + " products with cross-tenant categories. Unlinking...");
            int fixedProds = entityManager.createNativeQuery("""
                        UPDATE products p
                        JOIN categories c ON p.category_id = c.id
                        SET p.category_id = NULL
                        WHERE p.tenant_id != c.tenant_id
                    """).executeUpdate();
            System.out.println("✅ Fixed " + fixedProds + " products (category_id set to NULL).");
        } else {
            System.out.println("✅ No cross-tenant product categories found.");
        }

        // 3. Fix Products with Cross-Tenant Brands
        List<Object[]> crossProdBrands = entityManager.createNativeQuery("""
                    SELECT p.id, p.tenant_id, b.id, b.tenant_id
                    FROM products p
                    JOIN brands b ON p.brand_id = b.id
                    WHERE p.tenant_id != b.tenant_id
                """).getResultList();

        if (!crossProdBrands.isEmpty()) {
            System.out.println("Found " + crossProdBrands.size() + " products with cross-tenant brands. Unlinking...");
            int fixedBrands = entityManager.createNativeQuery("""
                        UPDATE products p
                        JOIN brands b ON p.brand_id = b.id
                        SET p.brand_id = NULL
                        WHERE p.tenant_id != b.tenant_id
                    """).executeUpdate();
            System.out.println("✅ Fixed " + fixedBrands + " products (brand_id set to NULL).");
        } else {
            System.out.println("✅ No cross-tenant product brands found.");
        }

        // 4. Fix Brands with Cross-Tenant Categories (Pivot Table)
        int fixedBrandCats = entityManager.createNativeQuery("""
                    DELETE bc FROM brands_categories bc
                    JOIN brands b ON bc.brand_id = b.id
                    JOIN categories c ON bc.category_id = c.id
                    WHERE b.tenant_id != c.tenant_id
                """).executeUpdate();
        if (fixedBrandCats > 0) {
            System.out.println("✅ Removed " + fixedBrandCats + " cross-tenant brand-category links.");
        } else {
            System.out.println("✅ No cross-tenant brand-category links found.");
        }

        // 5. Fix Orders with Cross-Tenant Customers
        // If an order belongs to Tenant A but the Customer belongs to Tenant B
        List<Object[]> crossOrders = entityManager.createNativeQuery("""
                    SELECT o.id, o.tenant_id, c.id, c.tenant_id
                    FROM orders o
                    JOIN customers c ON o.customer_id = c.id
                    WHERE o.tenant_id != c.tenant_id
                """).getResultList();

        if (!crossOrders.isEmpty()) {
            System.out.println("Found " + crossOrders.size() + " orders with cross-tenant customers. Deleting...");
            // Ideally we move them or fix them, but for now DELETE to stop 500 errors
            int fixedOrders = entityManager.createNativeQuery("""
                        DELETE o FROM orders o
                        JOIN customers c ON o.customer_id = c.id
                        WHERE o.tenant_id != c.tenant_id
                    """).executeUpdate();
            System.out.println("✅ Deleted " + fixedOrders + " cross-tenant orders.");
        } else {
            System.out.println("✅ No cross-tenant orders found.");
        }

        // 6. Fix Order Details with Cross-Tenant Products
        // If an order detail belongs to Tenant A (via Order) but Product is Tenant B
        List<Object[]> crossOrderDetails = entityManager.createNativeQuery("""
                    SELECT od.id, o.tenant_id, p.id, p.tenant_id
                    FROM order_details od
                    JOIN orders o ON od.order_id = o.id
                    JOIN products p ON od.product_id = p.id
                    WHERE o.tenant_id != p.tenant_id
                """).getResultList();

        if (!crossOrderDetails.isEmpty()) {
            System.out.println(
                    "Found " + crossOrderDetails.size() + " order details with cross-tenant products. Deleting...");
            int fixedDetails = entityManager.createNativeQuery("""
                        DELETE od FROM order_details od
                        JOIN orders o ON od.order_id = o.id
                        JOIN products p ON od.product_id = p.id
                        WHERE o.tenant_id != p.tenant_id
                    """).executeUpdate();
            System.out.println("✅ Deleted " + fixedDetails + " cross-tenant order details.");
        } else {
            System.out.println("✅ No cross-tenant order details found.");
        }

        System.out.println("=".repeat(80));
    }
}
