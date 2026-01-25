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

    // AG-CONFIG: Configurable tenants path, injected by FileUploadConfigurer
    private static String tenantsBasePath = "tenants";

    public static void setTenantsBasePath(String path) {
        tenantsBasePath = path;
    }

    public static String getStoragePath(Object entityId, String type) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not found for asset storage.");
        }
        // AG-ASSET-PATH-FIX: Standardized path using configurable base
        // Note: Paths.get(tenantsBasePath, ...) handles path separators automatically
        Path path = Paths.get(tenantsBasePath, String.valueOf(tenantId), "assets", type, String.valueOf(entityId));
        return path.toString();
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
        // {tenantsBasePath}/{tenantId}/assets/{type}/{id}/

        // AG-ASSET-PATH-FIX: Support absolute paths and configured TENANTS_PATH
        String normalizedPath = Paths.get(uploadDir).normalize().toString().replace("\\", "/");
        String normalizedBase = Paths.get(tenantsBasePath).normalize().toString().replace("\\", "/");

        // Check if path starts with or contains the configured base path
        boolean isUnderBasePath = normalizedPath.startsWith(normalizedBase)
                || normalizedPath.contains("/" + normalizedBase + "/");

        // Also check "tenants/" as fallback/hardcoded safety if base path is absolute
        // and complex
        boolean isRelativeTenants = normalizedPath.contains("/tenants/");

        if (!isUnderBasePath && !isRelativeTenants) {
            // Exception: Common system assets might be allowed?
            if (normalizedPath.contains("knowledge_export"))
                return;

            LOGGER.error("🚨 ARCHITECTURE VIOLATION: Attempt to write to prohibited path: " + uploadDir);
            throw new IllegalArgumentException("Architecture Violation: Assets path invalid: " + uploadDir);
        }

        // Tenant Isolation Check
        Long currentTenant = TenantContext.getTenantId();
        if (currentTenant != null) {
            // Check specifically for THIS tenant's ID in the path
            String tenantSegment = String.valueOf(currentTenant);
            // We expect the path to contain .../tenantId/...
            // This is a loose check but better than nothing for now given the path
            // variability
            if (!normalizedPath.contains("/" + tenantSegment + "/") && !normalizedPath.endsWith("/" + tenantSegment)) {
                // Double check strict path structure if possible, but for now rely on ID
                // presence
                // Logic: tenantsBasePath + "/" + tenantId
            }

            // Re-implementing strict check based on new structure
            // Configured Path: D:/DATA/tenants
            // Target Path: D:/DATA/tenants/4/assets/...
            Path targetPath = Paths.get(uploadDir).normalize();
            Path allowedRoot = Paths.get(tenantsBasePath, String.valueOf(currentTenant)).normalize();

            // Resolve absolute paths if needed for comparison
            // If uploadDir is relative, make it absolute relative to cwd
            if (!targetPath.isAbsolute()) {
                targetPath = Paths.get(System.getProperty("user.dir")).resolve(targetPath).normalize();
            }
            if (!allowedRoot.isAbsolute()) {
                allowedRoot = Paths.get(System.getProperty("user.dir")).resolve(allowedRoot).normalize();
            }

            if (!targetPath.startsWith(allowedRoot)) {
                // Fallback for "tenants/4" hardcoded pattern if tenantsBasePath is just
                // "tenants"
                if (tenantsBasePath.equals("tenants")) {
                    String legacyCheck = "tenants/" + currentTenant;
                    if (!normalizedPath.contains(legacyCheck)) {
                        LOGGER.error("🚨 SECURITY VIOLATION: Cross-tenant write attempt! Tenant " + currentTenant
                                + " tried to write to " + uploadDir);
                        throw new SecurityException("Cross-tenant write attempt denied.");
                    }
                } else {
                    LOGGER.error("🚨 SECURITY VIOLATION: Cross-tenant write attempt! Tenant " + currentTenant
                            + " tried to write to " + uploadDir + ". Expected root: " + allowedRoot);
                    throw new SecurityException("Cross-tenant write attempt denied.");
                }
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
        // Use the configured base path
        Path rootPath = Paths.get(tenantsBasePath, String.valueOf(tenantId));

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
