package com.onlineStore.admin.setting;

import com.onlineStore.admin.setting.service.FrontendSettingsService;
import com.onlineStore.admin.setting.settingBag.FrontendSettingBag;
import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class FrontendSettingsController {

    @Autowired
    private FrontendSettingsService service;

    @GetMapping("/settings/frontend")
    public String viewFrontendSettings(Model model,
                                       @org.springframework.security.core.annotation.AuthenticationPrincipal com.onlineStore.admin.security.StoreUserDetails userDetails) {
        FrontendSettingBag settings = service.getFrontendSettings();
        List<Setting> listSettings = settings.list();

        checkAndAddDefaults(listSettings, userDetails.getTenantId());

        for (Setting setting : listSettings) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }

        return "settings/frontend_settings";
    }

    private void checkAndAddDefaults(List<Setting> listSettings, Long tenantId) {
        addIfMissing(listSettings, "FRONTEND_MAINTENANCE_MODE", "false", tenantId);
        addIfMissing(listSettings, "FRONTEND_PAGINATION_SIZE", "10", tenantId);
        addIfMissing(listSettings, "FRONTEND_ENABLE_PUBLIC_API", "true", tenantId);
    }

    private void addIfMissing(List<Setting> list, String key, String defaultValue, Long tenantId) {
        boolean found = false;
        for (Setting s : list) {
            if (s.getKey().equals(key)) {
                found = true;
                break;
            }
        }
        if (!found) {
            Setting setting = new Setting(key, defaultValue, SettingCategory.FRONTEND);
            setting.setTenantId(tenantId);
            list.add(setting);
        }
    }

    @PostMapping("/settings/frontend/save")
    public String saveFrontendSettings(HttpServletRequest request, RedirectAttributes ra,
                                       @org.springframework.security.core.annotation.AuthenticationPrincipal com.onlineStore.admin.security.StoreUserDetails userDetails) {
        FrontendSettingBag settings = service.getFrontendSettings();
        List<Setting> listSettings = settings.list();
        Long tenantId = userDetails.getTenantId();

        saveSetting(listSettings, request, "FRONTEND_MAINTENANCE_MODE", tenantId);
        saveSetting(listSettings, request, "FRONTEND_PAGINATION_SIZE", tenantId);
        saveSetting(listSettings, request, "FRONTEND_ENABLE_PUBLIC_API", tenantId);

        service.saveAll(listSettings);
        ra.addFlashAttribute("message", "Frontend settings have been saved.");

        return "redirect:/settings/frontend";
    }

    private void saveSetting(List<Setting> list, HttpServletRequest request, String key, Long tenantId) {
        String value = request.getParameter(key);
        if (value != null) {
            updateSettingValue(list, key, value, tenantId);
        }
    }

    private void updateSettingValue(List<Setting> list, String key, String value, Long tenantId) {
        Setting setting = null;
        for (Setting s : list) {
            if (s.getKey().equals(key)) {
                setting = s;
                break;
            }
        }

        if (setting != null) {
            setting.setValue(value);
        } else {
            Setting settingNew = new Setting(key, value, SettingCategory.FRONTEND);
            settingNew.setTenantId(tenantId);
            list.add(settingNew);
        }
    }
}
