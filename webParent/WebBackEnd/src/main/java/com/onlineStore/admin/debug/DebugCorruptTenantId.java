package com.onlineStore.admin.debug;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.transaction.annotation.Transactional;

// @Component
public class DebugCorruptTenantId implements CommandLineRunner {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) throws Exception {
        System.out.println("🕵️ SEARCHING FOR CORRUPT TENANT ID: 6197085222132244817");

        // Use BigInteger to safe handle the long value in string
        String rogueId = "6197085222132244817";

        checkTable("users", rogueId);
        checkTable("categories", rogueId);
        checkTable("brands", rogueId);
        checkTable("products", rogueId);
        checkTable("customers", rogueId);

        // Also check FKs if possible?
        // Check if any product has a category with this tenant ID?
        // No, first let's see if this tenant ID exists in the tenant_id column of any
        // table.
    }

    private void checkTable(String table, String id) {
        try {
            long count = ((Number) entityManager.createNativeQuery(
                    "SELECT COUNT(*) FROM " + table + " WHERE tenant_id = " + id).getSingleResult()).longValue();

            if (count > 0) {
                System.out.println("❌ FOUND " + count + " rows in [" + table + "] with tenant_id=" + id);
            } else {
                System.out.println("✅ " + table + ": Clean");
            }
        } catch (Exception e) {
            System.err.println("Error checking " + table + ": " + e.getMessage());
        }
    }
}
