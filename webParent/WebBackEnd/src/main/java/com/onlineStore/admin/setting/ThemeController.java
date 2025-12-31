package com.onlineStore.admin.setting;

import com.onlineStore.admin.setting.service.SettingService;
import com.onlineStore.admin.setting.settingBag.ThemeSettingBag;
import com.onlineStoreCom.entity.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ThemeController {

    @Autowired
    private SettingService service;

    @GetMapping("/settings/themes")
    public String viewThemeSettings(Model model,
                                    @org.springframework.security.core.annotation.AuthenticationPrincipal com.onlineStore.admin.security.StoreUserDetails userDetails) {
        ThemeSettingBag themeSettings = service.getThemeSettings();
        List<Setting> listSettings = themeSettings.list();

        // Ensure defaults exist in the list (view only, unless saved)
        checkAndAddDefaults(listSettings, userDetails.getTenantId());

        for (Setting setting : listSettings) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }

        return "settings/theme_settings";
    }

    private void checkAndAddDefaults(List<Setting> listSettings, Long tenantId) {
        addIfMissing(listSettings, "THEME_COLOR_PRIMARY", "#007bff", tenantId);
        addIfMissing(listSettings, "THEME_COLOR_SECONDARY", "#6c757d", tenantId);
        addIfMissing(listSettings, "THEME_TABLE_HEADER_BG", "#343a40", tenantId);
        addIfMissing(listSettings, "THEME_TABLE_HEADER_COLOR", "#ffffff", tenantId);

        // Header & Footer Defaults
        addIfMissing(listSettings, "THEME_HEADER_BG", "#000000", tenantId);
        addIfMissing(listSettings, "THEME_HEADER_COLOR", "#ffffff", tenantId);
        addIfMissing(listSettings, "THEME_FOOTER_BG", "#343a40", tenantId);
        addIfMissing(listSettings, "THEME_FOOTER_COLOR", "#ffffff", tenantId);

        // Header Dropdown Defaults
        addIfMissing(listSettings, "THEME_HEADER_DROPDOWN_BG", "#343a40", tenantId);
        addIfMissing(listSettings, "THEME_HEADER_DROPDOWN_COLOR", "#ffffff", tenantId);
        addIfMissing(listSettings, "THEME_HEADER_DROPDOWN_HOVER_BG", "#6c757d", tenantId);
        addIfMissing(listSettings, "THEME_HEADER_DROPDOWN_HOVER_BG", "#6c757d", tenantId);
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
            Setting setting = new Setting(key, defaultValue, com.onlineStoreCom.entity.setting.SettingCategory.THEME);
            setting.setTenantId(tenantId);
            list.add(setting);
        }
    }

    @PostMapping("/settings/themes/save")
    public String saveThemeSettings(HttpServletRequest request, RedirectAttributes ra,
                                    @org.springframework.security.core.annotation.AuthenticationPrincipal com.onlineStore.admin.security.StoreUserDetails userDetails) {
        ThemeSettingBag themeSettings = service.getThemeSettings();
        List<Setting> listSettings = themeSettings.list();
        Long tenantId = userDetails.getTenantId();

        // Ensure we capture all theme settings, even if not in DB yet
        saveSetting(listSettings, request, "THEME_COLOR_PRIMARY", tenantId);
        saveSetting(listSettings, request, "THEME_COLOR_SECONDARY", tenantId);
        saveSetting(listSettings, request, "THEME_TABLE_HEADER_BG", tenantId);
        saveSetting(listSettings, request, "THEME_TABLE_HEADER_COLOR", tenantId);

        saveSetting(listSettings, request, "THEME_HEADER_BG", tenantId);
        saveSetting(listSettings, request, "THEME_HEADER_COLOR", tenantId);
        saveSetting(listSettings, request, "THEME_FOOTER_BG", tenantId);
        saveSetting(listSettings, request, "THEME_FOOTER_COLOR", tenantId);

        saveSetting(listSettings, request, "THEME_HEADER_DROPDOWN_BG", tenantId);
        saveSetting(listSettings, request, "THEME_HEADER_DROPDOWN_COLOR", tenantId);
        saveSetting(listSettings, request, "THEME_HEADER_DROPDOWN_HOVER_BG", tenantId);

        saveSetting(listSettings, request, "THEME_HEADER_HEIGHT", tenantId);
        saveSetting(listSettings, request, "THEME_FOOTER_HEIGHT", tenantId);
        saveSetting(listSettings, request, "THEME_LOGO_WIDTH", tenantId);
        saveSetting(listSettings, request, "THEME_FONT_FAMILY", tenantId);
        saveSetting(listSettings, request, "THEME_FONT_SIZE", tenantId);

        service.saveAll(listSettings);
        ra.addFlashAttribute("message", "Theme settings have been saved.");

        return "redirect:/settings/themes";
    }

    private void saveSetting(List<Setting> list, HttpServletRequest request, String key, Long tenantId) {
        String value = request.getParameter(key);
        if (value != null) {
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
                // Determine category - safest to use explicit if needed
                Setting settingNew = new Setting(key, value, com.onlineStoreCom.entity.setting.SettingCategory.THEME);
                settingNew.setTenantId(tenantId);
                list.add(settingNew);
            }
        }
    }
}
