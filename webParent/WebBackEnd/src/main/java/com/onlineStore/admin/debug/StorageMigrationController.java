package com.onlineStore.admin.debug;

import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStore.admin.security.StoreUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/debug/storage")
public class StorageMigrationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageMigrationController.class);

    @GetMapping("/migrate")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> runMigration(
            @RequestParam("tenantId") Long tenantId,
            @RequestParam(value = "type", defaultValue = "customers") String defaultType,
            @AuthenticationPrincipal StoreUserDetails loggedUser) {

        LOGGER.info("Manually triggered storage migration for Tenant {} by {}", tenantId, loggedUser.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("tenantId", tenantId);
        response.put("triggeredBy", loggedUser.getUsername());
        response.put("status", "STARTED");

        try {
            // Run synchronous migration (for now, simpler to debug if not async)
            FileUploadUtil.migrateLegacyFolders(tenantId, defaultType);
            response.put("status", "COMPLETED");
            response.put("message", "Migration utility finished. Check logs for details.");
        } catch (Exception e) {
            LOGGER.error("Migration failed", e);
            response.put("status", "FAILED");
            response.put("error", e.getMessage());
        }

        return response;
    }
}
