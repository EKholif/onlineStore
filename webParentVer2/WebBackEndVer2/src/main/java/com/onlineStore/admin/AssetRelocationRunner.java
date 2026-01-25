package com.onlineStore.admin;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * AG-ASSET-PATH-MIGRATION-001: Asset Path Standardization
 * WHY: All Entity methods (User, Product, Customer, etc.) use /assets/ in
 * paths:
 * e.g., /tenants/{id}/assets/users/{userId}/photo.jpg
 * WHAT: Move files FROM tenants/{id}/{type} TO tenants/{id}/assets/{type}
 * BUSINESS IMPACT: Ensures asset paths match entity getImagePath() methods
 */
@Component
@Order(5) // Run early in startup
public class AssetRelocationRunner implements CommandLineRunner {

    @org.springframework.beans.factory.annotation.Value("${app.storage.tenants-path}")
    private String tenantsBasePath;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==================================================");
        System.out.println("📂 STARTING ASSET PATH STANDARDIZATION (adding 'assets' folder)");
        System.out.println("==================================================");

        Path rootDir = Paths.get(tenantsBasePath);

        System.out.println("Configured tenants path: " + tenantsBasePath);
        System.out.println("Resolved tenants path: " + rootDir.toAbsolutePath());

        if (!Files.exists(rootDir)) {
            System.out.println("⚠️  Tenants directory not found at: " + rootDir.toAbsolutePath());
            System.out.println("⚠️  Skipping asset migration.");
            return;
        }

        try (Stream<Path> tenantDirs = Files.list(rootDir)) {
            tenantDirs.filter(Files::isDirectory).forEach(this::processTenant);
        }

        System.out.println("==================================================");
        System.out.println("✅ ASSET PATH STANDARDIZATION COMPLETED");
        System.out.println("==================================================");
    }

    private void processTenant(Path tenantDir) {
        System.out.println("Processing tenant: " + tenantDir.getFileName());

        // Asset types based on Entity structure
        String[] assetTypes = {"users", "products", "customers", "categories", "brands", "services"};

        for (String type : assetTypes) {
            Path wrongLocation = tenantDir.resolve(type); // tenants/4/products (WITHOUT assets)
            Path correctLocation = tenantDir.resolve("assets").resolve(type); // tenants/4/assets/products

            // Move from wrong location TO correct location (inside assets folder)
            if (Files.exists(wrongLocation) && !Files.exists(correctLocation)) {
                System.out.println("  ➡️  Moving " + type + " INTO assets folder...");
                moveIntoAssetsFolder(wrongLocation, correctLocation);
            } else if (Files.exists(correctLocation)) {
                System.out.println("  ✅ " + type + " already in correct location (assets folder)");
            } else if (!Files.exists(wrongLocation) && !Files.exists(correctLocation)) {
                System.out.println("  ⏭️  " + type + " not found (skipping)");
            }
        }
    }

    private void moveIntoAssetsFolder(Path source, Path target) {
        try {
            // Create assets/{type} directory structure
            Files.createDirectories(target.getParent());

            // Move entire directory
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("    ✅ Moved: " + source + " → " + target);

        } catch (IOException e) {
            System.err.println("    ❌ Failed to move " + source + ": " + e.getMessage());
        }
    }
}
