package com.onlineStore.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Order(100)
public class UserDiagnosticRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔍 [DIAGNOSTIC] Checking Schema...");

        // Check if 'user' and 'users' tables exist
        try {
            List<String> tables = jdbcTemplate.queryForList("SHOW TABLES", String.class);
            System.out.println("   📂 Tables Found: " + tables);

            if (tables.contains("user")) {
                Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM user", Integer.class);
                System.out.println("      ⚠️ Table 'user' EXISTS! Count: " + count);
            }
            if (tables.contains("users")) {
                Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM users", Integer.class);
                System.out.println("      ✅ Table 'users' EXISTS. Count: " + count);
            }

            // Check Constraints on user_roles
            System.out.println("   🔗 Checking 'user_roles' Constraints:");
            List<Map<String, Object>> constraints = jdbcTemplate.queryForList(
                    "SELECT CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME " +
                            "FROM information_schema.KEY_COLUMN_USAGE " +
                            "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_roles' AND REFERENCED_TABLE_NAME IS NOT NULL");

            for (Map<String, Object> c : constraints) {
                System.out.println("      > " + c.get("CONSTRAINT_NAME") +
                        " : " + c.get("COLUMN_NAME") +
                        " -> " + c.get("REFERENCED_TABLE_NAME") + "." + c.get("REFERENCED_COLUMN_NAME"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("🔍 [DIAGNOSTIC] Schema Check Complete.");
    }
}
