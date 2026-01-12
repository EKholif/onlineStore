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
        return "tenants/" + tenantId + "/assets/" + type + "/" + entityId;
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
        // tenants/{tenantId}/assets/{type}/
        if (!uploadDir.startsWith("tenants/")) {
            // Exception: Common system assets might be allowed?
            // For now, strict enforcement as per "Critical rules"
            // But we need to be careful about not breaking "Knowledge" export which might
            // use different paths?
            // Knowledge export uses "knowledge_export".
            if (uploadDir.startsWith("knowledge_export"))
                return;

            LOGGER.error("🚨 ARCHITECTURE VIOLATION: Attempt to write to prohibited path: " + uploadDir);
            throw new IllegalArgumentException("Architecture Violation: Assets must be stored in tenants/{id}/assets/");
        }

        // Tenant Isolation Check
        Long currentTenant = TenantContext.getTenantId();
        if (currentTenant != null) {
            String expectedStart = "tenants/" + currentTenant;
            if (!uploadDir.startsWith(expectedStart)) {
                LOGGER.error("🚨 SECURITY VIOLATION: Cross-tenant write attempt! Tenant " + currentTenant
                        + " tried to write to " + uploadDir);
                throw new SecurityException("Cross-tenant write attempt denied.");
            }
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
