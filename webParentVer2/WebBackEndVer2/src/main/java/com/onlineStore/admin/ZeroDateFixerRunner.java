package com.onlineStore.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE)
public class ZeroDateFixerRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔧 [ZeroDateFixer] STARTING: Fixing Zero Dates (0000-00-00) & Integrity Issues...");

        try {
            // Fix Duplicate Entry 32-1 in user_roles
            // This blocks startup if a persistent seeder tries to re-insert it.
            try {
                // AG-FIX: Cleaner approach - Delete orphaned user_roles validation
                jdbcTemplate.update("DELETE FROM user_roles WHERE user_id NOT IN (SELECT id FROM users)");
                System.out.println("   -> Fixed Integrity: Deleted orphaned user_roles (orphans cleaned).");

                // Specific fix just in case sync is off
                jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = 32 AND role_id = 1");
                jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = 34 AND role_id = 1");
            } catch (Exception ex) {
                System.err.println("   -> Integrity Fix Warning: " + ex.getMessage());
            }

            // Fix Users
            fixTable("users", "created_time");
            fixTable("users", "last_login_time");

            // Fix Customers
            fixTable("customers", "created_time");
            fixTable("customers", "last_login_time");
            fixTable("customers", "date_of_birth"); // Just in case

            // Fix Products
            fixTable("products", "created_time");
            fixTable("products", "updated_time");

            // Fix Orders
            fixTable("orders", "order_time");

            // Fix Brand
            fixTable("brands", "created_time");
            // Fix Category
            fixTable("categories", "created_time");

            // Fix Daily Product Stats (The culprit of earlier stack trace?)
            // Note: stats often use 'date' column which is DATE type, not DATETIME.
            fixDateColumn("daily_product_stats", "date");

            System.out.println("✅ [ZeroDateFixer] COMPLETED.");

        } catch (Exception e) {
            System.err.println("❌ [ZeroDateFixer] FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fixTable(String tableName, String columnName) {
        try {
            // Check if column exists first?
            // For now, simpler to just run update and catch exception if column missing.

            // Strategy: Update '0000-00-00 00:00:00' to NULL if nullable, or a default date
            // if not.
            // Safety first: Try setting to NULL.
            String sqlNull = "UPDATE " + tableName + " SET " + columnName + " = NULL WHERE CAST(" + columnName
                    + " AS CHAR) LIKE '0000-00-00%'";
            int rows = jdbcTemplate.update(sqlNull);
            System.out.println("   -> Fixed " + tableName + "." + columnName + " (Set to NULL): " + rows + " rows.");

        } catch (Exception e) {
            // If failed (e.g. not null constraint), try setting to explicit date
            try {
                String sqlDefault = "UPDATE " + tableName + " SET " + columnName
                        + " = '2000-01-01 00:00:00' WHERE CAST(" + columnName + " AS CHAR) LIKE '0000-00-00%'";
                int rows = jdbcTemplate.update(sqlDefault);
                System.out.println(
                        "   -> Fixed " + tableName + "." + columnName + " (Set to 2000-01-01): " + rows + " rows.");
            } catch (Exception ex) {
                System.err.println("   -> Failed to fix " + tableName + "." + columnName + ": " + ex.getMessage());
            }
        }
    }

    private void fixDateColumn(String tableName, String columnName) {
        try {
            String sqlDefault = "UPDATE " + tableName + " SET " + columnName + " = '2000-01-01' WHERE CAST("
                    + columnName + " AS CHAR) LIKE '0000-00-00%'";
            int rows = jdbcTemplate.update(sqlDefault);
            System.out.println(
                    "   -> Fixed " + tableName + "." + columnName + " (Set to 2000-01-01): " + rows + " rows.");
        } catch (Exception ex) {
            System.err.println("   -> Failed to fix " + tableName + "." + columnName + ": " + ex.getMessage());
        }
    }
}
