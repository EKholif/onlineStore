package com.onlineStore.admin.tenant;

import com.onlineStoreCom.entity.tenant.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/super-admin/tenants")
public class SuperAdminController {

    @Autowired
    private TenantService tenantService;

    // TODO: Add @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<Tenant> createTenant(@RequestBody TenantRegistrationRequest request) {
        Tenant tenant = tenantService.registerTenant(request);
        return ResponseEntity.ok(tenant);
    }
}
