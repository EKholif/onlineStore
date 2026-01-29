package com.onlineStore.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * AG-DATA-INTEGRITY-002: Audit Log Cleanup Runner
 * <p>
 * WHY: tenant_audit_log may contain orphan user_id references to deleted users,
 * which prevents FK constraint creation during schema updates.
 * <p>
 * BUSINESS IMPACT: Ensures Frontend can start successfully with ddl-auto=update
 * by cleaning orphan audit entries before Hibernate schema validation.
 * <p>
 * EXECUTION ORDER: Runs after ZeroDateFixer and SchemaFixer but before JPA initialization.
 */
@Component
@Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE + 2)
public class AuditLogCleanupRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🧹 [AuditLogCleanupRunner] Cleaning orphan audit log entries...");

        try {
            // Check if tenant_audit_log table exists
            String checkTableSql = "SELECT COUNT(*) FROM information_schema.TABLES " +
                    "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tenant_audit_log'";

            Integer tableExists = jdbcTemplate.queryForObject(checkTableSql, Integer.class);

            if (tableExists == null || tableExists == 0) {
                System.out.println("   ℹ️ tenant_audit_log table does not exist yet. Skipping cleanup.");
                return;
            }

            // Count orphan entries
            String countOrphansSql = "SELECT COUNT(*) FROM tenant_audit_log " +
                    "WHERE user_id IS NOT NULL AND user_id NOT IN (SELECT id FROM users)";

            Integer orphanCount = jdbcTemplate.queryForObject(countOrphansSql, Integer.class);

            if (orphanCount == null || orphanCount == 0) {
                System.out.println("   ✅ No orphan audit log entries found.");
                return;
            }

            System.out.println("   ⚠️ Found " + orphanCount + " orphan audit log entries (user_id references deleted users)");

            // Delete orphan entries
            String deleteSql = "DELETE FROM tenant_audit_log " +
                    "WHERE user_id IS NOT NULL AND user_id NOT IN (SELECT id FROM users)";

            int deletedRows = jdbcTemplate.update(deleteSql);

            System.out.println("   🗑️ Deleted " + deletedRows + " orphan audit log entries.");
            System.out.println("   ✅ Audit log cleanup complete.");

        } catch (Exception e) {
            System.err.println("   ❌ Audit Log Cleanup Failed: " + e.getMessage());
            e.printStackTrace();
            // Don't throw exception to allow app to continue startup
        }

        System.out.println("🧹 [AuditLogCleanupRunner] Complete.");
    }
}
