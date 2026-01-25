package com.onlineStore.admin.tenant;

import com.onlineStore.admin.audit.AuditLogService;
import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
public class TenantManagementRestController {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private AuditLogService auditLogService;

    /**
     * List all tenants (Root Only)
     */
    @GetMapping
    public List<Tenant> listTenants() {
        verifyRootAccess("listTenants");
        auditLogService.log("TENANT_MGT", "LIST", "Root viewed tenant list");
        return tenantService.listAll();
    }

    /**
     * Create/Register new Tenant
     */
    @PostMapping("/register")
    public Tenant registerTenant(@RequestBody Tenant tenant) {
        verifyRootAccess("registerTenant");
        Tenant created = tenantService.create(tenant);
        auditLogService.log("TENANT_MGT", "CREATE", "Root created tenant: " + created.getCode());
        return created;
    }

    /**
     * Update existing Tenant
     */
    @PutMapping("/{id}")
    public Tenant updateTenant(@PathVariable Long id, @RequestBody Tenant tenantDetails) {
        verifyRootAccess("updateTenant");
        // Logic to update tenant details (omitted for brevity, would delegate to service)
        // Ensure to handle status changes, etc.
        auditLogService.log("TENANT_MGT", "UPDATE", "Root updated tenant ID: " + id);
        return tenantDetails; // Placeholder
    }

    private void verifyRootAccess(String action) {
        Long current = TenantContext.getTenantId();
        if (current == null || current != 0L) {
            throw new SecurityException("⛔ ACCESS DENIED: Root Only Action: " + action);
        }
    }
}
