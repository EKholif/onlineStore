package com.onlineStore.admin.tenant;

import com.onlineStore.admin.setting.country.SettingRepository;
import com.onlineStore.admin.usersAndCustomers.users.UserRepository;
import com.onlineStore.admin.usersAndCustomers.users.role.RoleRepository;
import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.entity.tenant.TenantStatus;
import com.onlineStoreCom.entity.users.Role;
import com.onlineStoreCom.entity.users.User;
import com.onlineStoreCom.repo.TenantRepository;
import com.onlineStoreCom.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Tenant registerTenant(TenantRegistrationRequest request) {
        // 1. Create Tenant Record
        Tenant tenant = new Tenant();
        tenant.setName(request.getName());
        tenant.setCode(request.getCode());
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant.setCreatedAt(new Date());

        Tenant savedTenant = tenantRepository.save(tenant);

        // 2. Set Context to new Tenant to ensure subsequent entities are saved with
        // correct tenant_id
        // Note: We might need to manually set tenantId on entities if the
        // Filter/Listener isn't triggered here
        // But since we are in Service layer, we can rely on TenantContext +
        // EntityListener if configured,
        // OR manually set it. Given correct structure, let's manually set it to be safe
        // or rely on context.
        // The EntityListener checks TenantContext. Let's set it.
        Long previousTenant = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(savedTenant.getId());

            // 3. Create Admin Role for this Tenant
            Role adminRole = new Role("Admin", "Administrator for " + savedTenant.getName());
            // Force tenant ID if needed, but Context should handle it via EntityListener
            roleRepository.save(adminRole);

            // 4. Create Admin User
            User adminUser = new User();
            adminUser.setEmail(request.getAdminEmail());
            adminUser.setPassword(passwordEncoder.encode(request.getAdminPassword()));
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEnabled(true);
            adminUser.addRole(adminRole);

            userRepository.save(adminUser);

            // 5. Create Default Settings
            createDefaultSettings(savedTenant.getName());

        } finally {
            // Restore context
            if (previousTenant != null) {
                TenantContext.setTenantId(previousTenant);
            } else {
                TenantContext.clear();
            }
        }

        return savedTenant;
    }

    private void createDefaultSettings(String siteName) {
        // GENERAL Settings
        saveSetting("SITE_NAME", siteName, SettingCategory.GENERAL);
        saveSetting("SITE_LOGO", "default-logo.png", SettingCategory.GENERAL);
        saveSetting("COPYRIGHT", "Copyright (C) 2026 " + siteName, SettingCategory.GENERAL);

        // CURRENCY Settings (Default to USD)
        saveSetting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
        saveSetting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
        saveSetting("CURRENCY_SYMBOL_POSITION", "Before price", SettingCategory.CURRENCY);
        saveSetting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
        saveSetting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
        saveSetting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);
    }

    private void saveSetting(String key, String value, SettingCategory category) {
        Setting setting = new Setting(key, value, category);
        settingRepository.save(setting);
    }
}
