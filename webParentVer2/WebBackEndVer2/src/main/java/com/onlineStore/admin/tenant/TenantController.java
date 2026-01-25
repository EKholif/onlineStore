package com.onlineStore.admin.tenant;

import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @GetMapping("/manage")
    public String listTenants(Model model, RedirectAttributes ra) {
        Long currentTenantId = TenantContext.getTenantId();
        List<Tenant> tenants;

        if (currentTenantId != null && currentTenantId == 0L) {
            // Root sees ALL
            tenants = tenantService.listAll();
        } else {
            // Reseller sees CHILDREN only
            tenants = tenantService.listChildren(currentTenantId);
        }

        model.addAttribute("tenants", tenants);
        model.addAttribute("pageTitle", "Tenant Management");

        return "tenants/tenant_list";
    }

    private boolean isRoot() {
        Long tenantId = TenantContext.getTenantId();
        return tenantId != null && tenantId == 0L;
    }
}
