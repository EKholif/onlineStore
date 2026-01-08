package com.onlineStore.admin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class SchemaInspectionTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void inspectProductsTable() {
        Query query = entityManager.createNativeQuery("SHOW COLUMNS FROM products");
        List<Object[]> columns = query.getResultList();
        System.out.println("Product Columns:");
        for (Object[] row : columns) {
            String field = (String) row[0];
            String type = (String) row[1];
            String nullability = (String) row[2];
            String key = (String) row[3];
            String defaultVal = (row[4] != null) ? row[4].toString() : "NULL";
            System.out.println("Field: " + field + ", Type: " + type + ", Null: " + nullability + ", Key: " + key
                    + ", Default: " + defaultVal);
        }

        System.out.println("\n--------------------------------------------------\n");

        Query query2 = entityManager.createNativeQuery("SHOW COLUMNS FROM categories");
        List<Object[]> columns2 = query2.getResultList();
        System.out.println("Category Columns:");
        for (Object[] row : columns2) {
            String field = (String) row[0];
            String type = (String) row[1];
            String nullability = (String) row[2];
            String key = (String) row[3];
            String defaultVal = (row[4] != null) ? row[4].toString() : "NULL";
            System.out.println("Field: " + field + ", Type: " + type + ", Null: " + nullability + ", Key: " + key
                    + ", Default: " + defaultVal);
        }
    }
}
