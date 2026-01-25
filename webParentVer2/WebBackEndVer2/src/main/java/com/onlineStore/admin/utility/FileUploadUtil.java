package com.onlineStore.admin.utility;

import com.onlineStoreCom.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {

    public static String getStoragePath(Object entityId, String type) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not found for asset storage.");
        }
        // AG-ASSET-PATH-FIX: Standardized path
        String path = "tenants/" + tenantId + "/assets/" + type + "/" + entityId;
        return path;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);

    public static void saveFile(String uploadDir, String filename,
            MultipartFile multipartFile) throws IOException {

        validatePath(uploadDir);

        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {

            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(filename);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException ex) {

            LOGGER.error("could not save the file" + filename, ex);
            throw new IOException("could not save the file" + filename, ex);
        }

    }

    private static void validatePath(String uploadDir) {
        // Architecture Rule: Assets MUST be stored only under
        // tenants/{tenantId}/assets/{type}/{id}/

        // AG-ASSET-PATH-FIX: Support absolute paths and configured TENANTS_PATH
        String normalizedPath = Paths.get(uploadDir).normalize().toString().replace("\\", "/");
        boolean isRelativeTenants = normalizedPath.startsWith("tenants/");
        boolean containsTenantsSegment = normalizedPath.contains("/tenants/");

        if (!isRelativeTenants && !containsTenantsSegment) {
            // Exception: Common system assets might be allowed?
            if (normalizedPath.contains("knowledge_export"))
                return;

            LOGGER.error("🚨 ARCHITECTURE VIOLATION: Attempt to write to prohibited path: " + uploadDir);
            throw new IllegalArgumentException("Architecture Violation: Assets path invalid: " + uploadDir);
        }

        // Tenant Isolation Check
        Long currentTenant = TenantContext.getTenantId();
        if (currentTenant != null) {
            String tenantSegment = "tenants/" + currentTenant;
            if (!normalizedPath.contains(tenantSegment)) {
                LOGGER.error("🚨 SECURITY VIOLATION: Cross-tenant write attempt! Tenant " + currentTenant
                        + " tried to write to " + uploadDir);
                throw new SecurityException("Cross-tenant write attempt denied.");
            }
        }
    }

    /**
     * Migration Tool: Attempt to rename folders from old structure (tenants/4/11)
     * to new structure (tenants/4/customers/11).
     * WARNING: This assumes ID collision is handled or unlikely for minimal data.
     * Manual review recommended if IDs overlap between types.
     */
    public static void migrateLegacyFolders(Long tenantId, String defaultType) {
        LOGGER.info("Starting migration for tenant: " + tenantId);
        String tenantRoot = "tenants/" + tenantId;
        Path rootPath = Paths.get(tenantRoot);

        if (!Files.exists(rootPath))
            return;

        try {
            Files.list(rootPath).forEach(path -> {
                if (Files.isDirectory(path)) {
                    String dirName = path.getFileName().toString();
                    // If directory name is just a number, it's a legacy folder
                    if (dirName.matches("\\d+")) {
                        try {
                            // Strategy: Move to 'customers' by default or 'users' if specified
                            // This is a naive heuristic for emergency fix.
                            String targetType = defaultType != null ? defaultType : "customers";
                            Path targetDir = rootPath.resolve(targetType).resolve(dirName);

                            if (!Files.exists(targetDir.getParent())) {
                                Files.createDirectories(targetDir.getParent());
                            }

                            LOGGER.info("Migrating legacy folder: " + path + " -> " + targetDir);
                            Files.move(path, targetDir, StandardCopyOption.ATOMIC_MOVE);

                        } catch (IOException e) {
                            LOGGER.error("Failed to migrate folder: " + path, e);
                        }
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("Migration directory scan failed", e);
        }
    }

    public static void cleanDir(String dir) throws IOException {

        validatePath(dir);

        Path dirPath = Paths.get(dir);

        try {
            Files.list(dirPath).forEach(file -> {
                if (!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);
                    } catch (IOException ex) {
                        LOGGER.error("File not found: " + file.getFileName());
                    }
                }
            });

        } catch (IOException e) {
            LOGGER.error("Not a valid directory: " + dir);
        }
    }

    public static void deleteDir(String dir) throws IOException {

        cleanDir(dir);

        try {
            Files.delete(Paths.get(dir));

        } catch (IOException e) {
            LOGGER.error("Could not remove directory: " + dir);
        }

    }

}
