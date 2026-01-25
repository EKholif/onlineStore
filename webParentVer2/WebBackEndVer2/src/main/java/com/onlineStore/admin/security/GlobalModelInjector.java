package com.onlineStore.admin.security;

import com.onlineStore.admin.usersAndCustomers.users.servcies.UserService;
import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.entity.users.User;
import com.onlineStoreCom.entity.users.UserTenant;
import com.onlineStoreCom.repo.TenantRepository;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalModelInjector {

    @Autowired
    private UserService userService; // Ensure UserService has findByEmail

    @Autowired
    private TenantRepository tenantRepo;

    @ModelAttribute("currentTenant")
    public Tenant getCurrentTenant() {
        Long id = TenantContext.getTenantId();
        if (id == null)
            return null;
        return tenantRepo.findById(id).orElse(null);
    }

    @ModelAttribute("availableTenants")
    public List<Tenant> getAvailableTenants(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return new ArrayList<>();
        }

        String email = auth.getName();
        User user = userService.getByEmail(email); // Assuming this method exists

        if (user == null)
            return new ArrayList<>();

        // [AG-HIERARCHY] Return all tenants this user is linked to
        return user.getTenants().stream()
                .map(UserTenant::getTenant)
                .collect(Collectors.toList());
    }

    @ModelAttribute("isSuperAdmin")
    public boolean isSuperAdmin(HttpServletRequest request) {
        // Simple check if user is in Tenant 0 context currently
        Long id = TenantContext.getTenantId();
        return id != null && id == 0L;
    }
}
