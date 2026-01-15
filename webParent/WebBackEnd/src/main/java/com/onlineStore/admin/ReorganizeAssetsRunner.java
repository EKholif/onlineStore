package com.onlineStore.admin;

import com.onlineStoreCom.entity.product.Product;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Component
@Order(10) // Run after migration
public class ReorganizeAssetsRunner implements CommandLineRunner {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) throws Exception {
        System.out.println("📂 STARTING ASSET REORGANIZATION...");

        List<Product> products = entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();

        System.out.println("Processing " + products.size() + " products...");

        for (Product p : products) {
            if (p.getMainImage() == null)
                continue;

            Long tenantId = p.getTenantId();
            Integer id = p.getId();

            if (tenantId == null)
                tenantId = 0L; // Safety default

            Path expectedDir = Path.of("tenants", String.valueOf(tenantId), "assets", "products", String.valueOf(id));

            // Candidate Legacy Paths
            List<Path> candidates = List.of(
                    Path.of("products-photos", String.valueOf(id)),
                    Path.of("_legacy_backup", "root_backup", "products-photos", String.valueOf(id)),
                    Path.of("tenants", "0", "assets", "products", String.valueOf(id)), // Wrong tenant location
                    Path.of("tenants", "null", "assets", "products", String.valueOf(id)));

            for (Path oldDir : candidates) {
                if (Files.exists(oldDir) && !Files.exists(expectedDir)) {
                    System.out.println(
                            "   > Moving assets for Product " + id + " from [" + oldDir + "] to [" + expectedDir + "]");
                    moveDirectory(oldDir, expectedDir);
                } else if (Files.exists(oldDir) && Files.exists(expectedDir)) {
                    // Merge? Or just log
                    System.out.println("   > MERGE required? Found assets at [" + oldDir + "] AND [" + expectedDir
                            + "]. Skipping (Manual check needed).");
                }
            }
        }
        System.out.println("📂 REORGANIZATION COMPLETE.");
    }

    private void moveDirectory(Path source, Path target) {
        try {
            // Create target parent if needed
            Files.createDirectories(target.getParent());

            // Move the directory itself (rename)
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("❌ Failed to move: " + e.getMessage());
        }
    }
}
