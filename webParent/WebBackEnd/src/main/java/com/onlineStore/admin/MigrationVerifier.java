package com.onlineStore.admin;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(2) // Run after the corrector
public class MigrationVerifier implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationVerifier.class);

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) throws Exception {
        LOGGER.info("🕵️ STARTING MIGRATION VERIFICATION 🕵️");
        StringBuilder report = new StringBuilder();
        report.append("VERIFICATION REPORT\n");
        report.append("===================\n");

        checkTable("products", report);
        checkTable("categories", report);
        checkTable("brands", report);
        checkTable("customers", report);
        checkTable("orders", report);
        checkTable("reviews", report);
        checkTable("articles", report);
        checkTable("menus", report);
        checkTable("sections", report);
        checkTable("settings", report);

        java.nio.file.Files.writeString(java.nio.file.Path.of("verification_result.txt"), report.toString());
        LOGGER.info("🕵️ VERIFICATION COMPLETE. Check verification_result.txt 🕵️");
    }

    private void checkTable(String tableName, StringBuilder report) {
        try {
            long tenant1Count = ((Number) entityManager
                    .createNativeQuery("SELECT COUNT(*) FROM " + tableName + " WHERE tenant_id = 1").getSingleResult())
                    .longValue();
            long tenant4Count = ((Number) entityManager
                    .createNativeQuery("SELECT COUNT(*) FROM " + tableName + " WHERE tenant_id = 4").getSingleResult())
                    .longValue();

            String status = (tenant1Count > 0) ? "❌ FOUND TENANT 1 DATA!" : "✅ Clean";
            String line = String.format("[%s] Tenant 1: %d | Tenant 4: %d | Status: %s\n",
                    String.format("%-15s", tableName),
                    tenant1Count, tenant4Count, status);
            report.append(line);
            LOGGER.info(line.trim());
        } catch (Exception e) {
            report.append("Could not verify table " + tableName + ": " + e.getMessage() + "\n");
        }
    }
}
