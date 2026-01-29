package com.onlineStore.admin.config;

import com.onlineStore.admin.MvcConfig;
import com.onlineStore.admin.utility.FileUploadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * AG-STORAGE-001: Storage Path Consistency Validator (GOLDEN RULE)
 * <p>
 * WHY: Prevents 404 errors by ensuring FileUploadUtil (write) and MvcConfig (read)
 * use identical storage paths. Application MUST NOT start if paths are inconsistent.
 * <p>
 * BUSINESS IMPACT: Critical for image serving, prevents silent data loss from path mismatches.
 * <p>
 * ENFORCEMENT: Fails startup with clear error message if paths don't match.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StoragePathValidator implements CommandLineRunner {

    @Value("${app.storage.tenants-path}")
    private String configuredPath;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔍 [AG-STORAGE-001] Validating storage path consistency...");

        // Get resolved absolute paths
        Path uploadPath = Paths.get(FileUploadUtil.getResolvedBasePath()).toAbsolutePath().normalize();
        Path mvcPath = Paths.get(MvcConfig.getResolvedTenantsPath()).toAbsolutePath().normalize();

        // Check consistency
        if (!uploadPath.equals(mvcPath)) {
            String error = String.format(
                    "\n" +
                            "═══════════════════════════════════════════════════════════════════════\n" +
                            "🚨 AG-STORAGE-001 VIOLATION: Storage Path Mismatch Detected!\n" +
                            "═══════════════════════════════════════════════════════════════════════\n" +
                            "\n" +
                            "  FileUploadUtil (write): %s\n" +
                            "  MvcConfig (HTTP serve): %s\n" +
                            "\n" +
                            "IMPACT: Images uploaded won't be accessible via HTTP (404 errors).\n" +
                            "\n" +
                            "ROOT CAUSE: TENANTS_PATH environment variable is set, overriding config.\n" +
                            "\n" +
                            "FIX:\n" +
                            "  1. IntelliJ: Run → Edit Configurations\n" +
                            "  2. Select 'WebBackEndApplication'\n" +
                            "  3. Environment Variables → DELETE 'TENANTS_PATH' entry\n" +
                            "  4. Click OK and restart application\n" +
                            "\n" +
                            "GOLDEN RULE: Never set TENANTS_PATH in run configurations.\n" +
                            "             Use default relative path 'tenants' from application.properties.\n" +
                            "\n" +
                            "═══════════════════════════════════════════════════════════════════════\n",
                    uploadPath,
                    mvcPath
            );

            throw new IllegalStateException(error);
        }

        System.out.println("✅ [AG-STORAGE-001] Storage path validated: " + uploadPath);
    }
}
