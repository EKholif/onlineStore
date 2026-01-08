package com.onlineStore.admin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class SchemaFixTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void dropZombieColumns() {
        dropColumnIfExists("products", "enable");
        dropColumnIfExists("categories", "enable");
        dropColumnIfExists("users", "enable");
    }

    @Transactional
    public void dropColumnIfExists(String tableName, String columnName) {
        try {
            Query checkQuery = entityManager.createNativeQuery(
                    "SELECT count(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = '"
                            + tableName + "' AND COLUMN_NAME = '" + columnName + "'");
            Number count = (Number) checkQuery.getSingleResult();
            if (count.intValue() > 0) {
                System.out.println("Column '" + columnName + "' exists in table '" + tableName + "'. Dropping...");
                entityManager.createNativeQuery("ALTER TABLE " + tableName + " DROP COLUMN " + columnName)
                        .executeUpdate();
                System.out.println("Successfully dropped column '" + columnName + "' from table '" + tableName + "'.");
            } else {
                System.out
                        .println("Column '" + columnName + "' does not exist in table '" + tableName + "'. Skipping.");
            }
        } catch (Exception e) {
            System.out.println("Failed to drop column '" + columnName + "' from table '" + tableName + "'. Error: "
                    + e.getMessage());
        }
    }
}
