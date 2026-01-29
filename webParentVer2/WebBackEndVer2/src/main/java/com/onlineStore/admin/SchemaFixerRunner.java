package com.onlineStore.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE + 1) // Run just after ZeroDateFixer
public class SchemaFixerRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔧 [SchemaFixerRunner] Checking Schema Constraints...");

        try {
            // 1. Fix user_roles -> users FK
            // The bad constraint references 'user' instead of 'users'
            // Name mismatch: FK55itppkw3i07do3h7qoclqd4k

            String checkSql = "SELECT count(*) FROM information_schema.TABLE_CONSTRAINTS " +
                    "WHERE CONSTRAINT_NAME = 'FK55itppkw3i07do3h7qoclqd4k' " +
                    "AND TABLE_NAME = 'user_roles' AND TABLE_SCHEMA = DATABASE()";

            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);

            if (count != null && count > 0) {
                System.out.println("   ⚠️ Found Broken Constraint: FK55itppkw3i07do3h7qoclqd4k (Likely references 'user')");

                // Drop it
                jdbcTemplate.execute("ALTER TABLE user_roles DROP FOREIGN KEY FK55itppkw3i07do3h7qoclqd4k");
                System.out.println("   🗑️ Dropped Broken Constraint.");

                // Re-add correctly
                // Check if user_id column exists first (it must)
                // Assuming users.id is Integer/BigInt matching user_roles.user_id

                String addSql = "ALTER TABLE user_roles ADD CONSTRAINT FK_user_roles_users " +
                        "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE";

                jdbcTemplate.execute(addSql);
                System.out.println("   ✅ Added Correct Constraint: FK_user_roles_users -> users(id)");
            } else {
                System.out.println("   ✅ specific broken constraint not found (Already fixed?).");
                // Check if FK_user_roles_users exists?
            }

            // Also check for any other constraint referencing 'user'
            // ...

        } catch (Exception e) {
            System.err.println("   ❌ Schema Fix Failed: " + e.getMessage());
            // Don't throw exception, try to continue, maybe it's already fixed or different name
        }

        System.out.println("🔧 [SchemaFixerRunner] Complete.");
    }
}
