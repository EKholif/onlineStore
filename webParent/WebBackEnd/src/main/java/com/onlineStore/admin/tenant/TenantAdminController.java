package com.onlineStore.admin.tenant;

import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.entity.tenant.TenantStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/tenants")
public class TenantAdminController {

    @Autowired
    private TenantService tenantService;

    @GetMapping
    public String listTenants(Model model) {
        List<Tenant> tenants = tenantService.listAllTenants();
        model.addAttribute("tenants", tenants);
        return "tenant/tenants";
    }

    @GetMapping("/{id}/enable")
    public String enableTenant(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            tenantService.updateTenantStatus(id, TenantStatus.ACTIVE);
            ra.addFlashAttribute("message", "Tenant enabled successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error enabling tenant: " + e.getMessage());
        }
        return "redirect:/admin/tenants";
    }

    @GetMapping("/{id}/suspend")
    public String suspendTenant(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            tenantService.updateTenantStatus(id, TenantStatus.SUSPENDED);
            ra.addFlashAttribute("message", "Tenant suspended successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error suspending tenant: " + e.getMessage());
        }
        return "redirect:/admin/tenants";
    }
}
