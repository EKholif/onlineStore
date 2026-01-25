package com.onlineStore.admin;

import org.springframework.boot.CommandLineRunner;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

/**
 * Runners to move files from `tenants/{id}/assets/{type}` to
 * `tenants/{id}/{type}`.
 */
// @Component
public class AssetRelocationRunner implements CommandLineRunner {

    private final Path rootDir = Paths.get("tenants");

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==================================================");
        System.out.println("STARTING ASSET RELOCATION (removing 'assets' folder)");
        System.out.println("==================================================");

        if (!Files.exists(rootDir)) {
            System.out.println("Root 'tenants' directory not found.");
            return;
        }

        try (Stream<Path> tenantDirs = Files.list(rootDir)) {
            tenantDirs.filter(Files::isDirectory).forEach(this::processTenant);
        }

        System.out.println("==================================================");
        System.out.println("ASSET RELOCATION COMPLETED");
        System.out.println("==================================================");
    }

    private void processTenant(Path tenantDir) {
        Path assetsDir = tenantDir.resolve("assets");
        if (!Files.exists(assetsDir)) {
            return;
        }

        System.out.println("Processing tenant: " + tenantDir.getFileName());

        try (Stream<Path> typeDirs = Files.list(assetsDir)) {
            typeDirs.forEach(typeDir -> {
                Path relativeTypeDir = assetsDir.relativize(typeDir); // e.g. "products"
                Path targetTypeDir = tenantDir.resolve(relativeTypeDir); // tenants/4/products

                System.out.println("  Migrating type: " + relativeTypeDir);
                moveRecursively(typeDir, targetTypeDir);
            });
        } catch (IOException e) {
            System.err.println("  Error listing assets dir: " + e.getMessage());
        }

        // Try to delete assets dir if empty
        try {
            if (Files.list(assetsDir).findAny().isEmpty()) {
                Files.delete(assetsDir);
                System.out.println("  Deleted empty assets dir: " + assetsDir);
            } else {
                System.out.println("  Assets dir not empty, skipping delete: " + assetsDir);
            }
        } catch (IOException e) {
            // ignore
        }
    }

    private void moveRecursively(Path source, Path target) {
        try {
            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path rel = source.relativize(dir);
                    Path targetDir = target.resolve(rel);
                    if (!Files.exists(targetDir)) {
                        Files.createDirectories(targetDir);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path rel = source.relativize(file);
                    Path targetFile = target.resolve(rel);

                    // Move and replace
                    Files.move(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("    Moved: " + file.getFileName());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir); // Delete empty dir after processing
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("    Failed to move " + source + ": " + e.getMessage());
        }
    }
}
