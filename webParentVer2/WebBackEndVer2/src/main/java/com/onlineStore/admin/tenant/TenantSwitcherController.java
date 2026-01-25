package com.onlineStore.admin.tenant;

import com.onlineStore.admin.audit.AuditService;
import com.onlineStore.admin.usersAndCustomers.users.servcies.UserService;
import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.entity.users.User;
import com.onlineStoreCom.repo.TenantRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AG-TENANT-SWITCH-001: Tenant Switcher Controller
 * <p>
 * Purpose:
 * - Provides REST API for tenant switching in admin panel
 * - Validates access before switching
 * - Manages session-based tenant context
 * - Logs all switch attempts for audit
 * <p>
 * Business Impact:
 * - Platform/Agency Admins can switch to child tenants
 * - Tenant Admin cannot switch (returns 403)
 * - All switches are audited
 * - Session-based (no JWT regeneration needed)
 */
@RestController
@RequestMapping("/api/tenant-switcher")
public class TenantSwitcherController {

    @Autowired
    private UserService userService;

    @Autowired
    private TenantRepository tenantRepo;

    @Autowired
    private AuditService auditService;

    /**
     * AG-API-ACCESSIBLE-001: Get accessible tenants for dropdown
     * <p>
     * GET /api/tenant-switcher/accessible-tenants
     * <p>
     * Returns:
     * - Platform Admin: ALL tenants
     * - Agency Admin: Own + Child tenants
     * - Tenant Admin: Only own tenant
     */
    @GetMapping("/accessible-tenants")
    public ResponseEntity<List<Map<String, Object>>> getAccessibleTenants(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        User user = userService.getByEmail(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(404).build(); // User not found
        }

        List<Tenant> accessibleTenants = userService.getAccessibleTenants(user);

        // Map to simple DTO for JSON response
        List<Map<String, Object>> tenantDtos = accessibleTenants.stream()
                .map(tenant -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", tenant.getId());
                    dto.put("name", tenant.getName());
                    dto.put("code", tenant.getCode());
                    dto.put("level", tenant.getLevel());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(tenantDtos);
    }

    /**
     * AG-API-SWITCH-001: Switch to target tenant
     * <p>
     * POST /api/tenant-switcher/switch?tenantId=X
     * <p>
     * Workflow:
     * 1. Validate user has access to target tenant
     * 2. Store ORIGINAL_TENANT_ID on first switch
     * 3. Update SWITCHED_TENANT_ID in session
     * 4. Log switch to audit
     * 5. Return success
     */
    @PostMapping("/switch")
    public ResponseEntity<Map<String, String>> switchTenant(
            @RequestParam("tenantId") Long tenantId,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session,
            HttpServletRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        User user = userService.getByEmail(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        // Validate access
        if (!userService.canAccessTenant(user, tenantId)) {
            // Log denied attempt
            Tenant currentTenant = getCurrentTenant(user);
            Tenant targetTenant = tenantRepo.findById(tenantId).orElse(null);
            auditService.logAccessDenied(user, currentTenant, targetTenant,
                    "UNAUTHORIZED_TENANT_SWITCH", request);

            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        Tenant targetTenant = tenantRepo.findById(tenantId).orElse(null);
        if (targetTenant == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Tenant not found"));
        }

        // Store original tenant ID on first switch
        if (session.getAttribute("ORIGINAL_TENANT_ID") == null) {
            Tenant originalTenant = getCurrentTenant(user);
            if (originalTenant != null) {
                session.setAttribute("ORIGINAL_TENANT_ID", originalTenant.getId());
            }
        }

        // Update switched tenant ID
        session.setAttribute("SWITCHED_TENANT_ID", tenantId);

        // Log successful switch
        Tenant fromTenant = getCurrentTenant(user);
        auditService.logTenantSwitch(user, fromTenant, targetTenant, request);

        return ResponseEntity.ok(Map.of(
                "success", "true",
                "message", "Switched to tenant: " + targetTenant.getName(),
                "tenantId", tenantId.toString()
        ));
    }

    /**
     * AG-API-RESET-001: Return to original tenant
     * <p>
     * POST /api/tenant-switcher/reset
     * <p>
     * Clears SWITCHED_TENANT_ID and returns to user's home tenant
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetToOriginalTenant(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session,
            HttpServletRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        User user = userService.getByEmail(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        Long originalTenantId = (Long) session.getAttribute("ORIGINAL_TENANT_ID");
        Long currentTenantId = (Long) session.getAttribute("SWITCHED_TENANT_ID");

        // Clear switched tenant
        session.removeAttribute("SWITCHED_TENANT_ID");
        session.removeAttribute("ORIGINAL_TENANT_ID");

        // Log reset
        if (currentTenantId != null && originalTenantId != null) {
            Tenant from = tenantRepo.findById(currentTenantId).orElse(null);
            Tenant to = tenantRepo.findById(originalTenantId).orElse(null);
            if (from != null && to != null) {
                auditService.logTenantSwitch(user, from, to, request);
            }
        }

        Tenant originalTenant = getCurrentTenant(user);
        String tenantName = originalTenant != null ? originalTenant.getName() : "your tenant";

        return ResponseEntity.ok(Map.of(
                "success", "true",
                "message", "Returned to " + tenantName
        ));
    }

    /**
     * Helper: Get user's current effective tenant
     */
    private Tenant getCurrentTenant(User user) {
        if (user.getTenants() == null || user.getTenants().isEmpty()) {
            return null;
        }
        return user.getTenants().iterator().next().getTenant();
    }
}
