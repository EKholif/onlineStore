package com.onlineStore.admin.debug;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * [AG-DEBUG-CROSS-PARENT-001] Find ALL cross-tenant parent references
 * <p>
 * WHY: Categories may have parent from different tenant (violates isolation)
 * GOAL: Find ALL categories where parent.tenant_id != child.tenant_id
 */
@Component
@Order(2) // Run after other diagnostics
public class FindCrossTenantParents implements CommandLineRunner {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) throws Exception {
        System.out.println("=".repeat(80));
        System.out.println("🔍 CROSS-TENANT PARENT CHECK (ALL TENANTS)");
        System.out.println("=".repeat(80));

        // Find ALL categories with cross-tenant parent
        var crossParents = entityManager.createNativeQuery("""
                    SELECT c.id, c.name, c.tenant_id AS child_tenant,
                           p.id AS parent_id, p.name AS parent_name, p.tenant_id AS parent_tenant
                    FROM categories c
                    JOIN categories p ON c.parent_id = p.id
                    WHERE c.tenant_id != p.tenant_id
                    ORDER BY c.tenant_id, p.tenant_id
                """).getResultList();

        System.out.println("\n⚠️ Categories with CROSS-TENANT parents: " + crossParents.size());
        for (Object row : crossParents) {
            Object[] cols = (Object[]) row;
            System.out.println("  ❌ Category #" + cols[0] + " (" + cols[1] + ") [Tenant " + cols[2] + "]");
            System.out.println("     → Parent: #" + cols[3] + " (" + cols[4] + ") [Tenant " + cols[5] + "]");
        }

        // Check if Tenant 4 specifically has any cross-tenant parents
        var tenant4CrossParents = entityManager.createNativeQuery("""
                    SELECT c.id, c.name, p.id, p.name, p.tenant_id
                    FROM categories c
                    JOIN categories p ON c.parent_id = p.id
                    WHERE c.tenant_id = 4 AND c.tenant_id != p.tenant_id
                """).getResultList();

        System.out.println("\n📌 Tenant 4 categories with cross-tenant parents: " + tenant4CrossParents.size());
        for (Object row : tenant4CrossParents) {
            Object[] cols = (Object[]) row;
            System.out.println("  ❌ Category #" + cols[0] + " (" + cols[1] + ") → Parent #" + cols[2] + " (" + cols[3]
                    + ") from Tenant " + cols[4]);
        }

        // Check orphan parents (parent_id is set but parent doesn't exist)
        var orphanParents = entityManager.createNativeQuery("""
                    SELECT c.id, c.name, c.tenant_id, c.parent_id
                    FROM categories c
                    WHERE c.parent_id IS NOT NULL
                    AND c.parent_id NOT IN (SELECT id FROM categories)
                """).getResultList();

        System.out.println("\n🔗 Categories with orphan parent_id: " + orphanParents.size());

        System.out.println("\n" + "=".repeat(80));
    }
}
