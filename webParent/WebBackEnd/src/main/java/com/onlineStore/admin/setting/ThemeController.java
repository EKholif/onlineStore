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

        // Colors
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

        // Dimensions with px suffix
        saveSettingWithSuffix(listSettings, request, "THEME_HEADER_HEIGHT", "px", tenantId);
        saveSettingWithSuffix(listSettings, request, "THEME_FOOTER_HEIGHT", "px", tenantId);
        saveSettingWithSuffix(listSettings, request, "THEME_LOGO_WIDTH", "px", tenantId);
        saveSettingWithSuffix(listSettings, request, "THEME_FONT_SIZE", "px", tenantId);

        // Font Weight (Checkbox handling)
        String weight = request.getParameter("THEME_FONT_WEIGHT");
        updateSettingValue(listSettings, "THEME_FONT_WEIGHT", weight == null ? "normal" : weight, tenantId);

        service.saveAll(listSettings);
        ra.addFlashAttribute("message", "Theme settings have been saved.");

        return "redirect:/settings/themes";
    }

    private void saveSetting(List<Setting> list, HttpServletRequest request, String key, Long tenantId) {
        String value = request.getParameter(key);
        if (value != null) {
            updateSettingValue(list, key, value, tenantId);
        }
    }

    private void saveSettingWithSuffix(List<Setting> list, HttpServletRequest request, String key, String suffix,
                                       Long tenantId) {
        String value = request.getParameter(key);
        if (value != null && !value.isEmpty()) {
            updateSettingValue(list, key, value + suffix, tenantId);
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
            Setting settingNew = new Setting(key, value, com.onlineStoreCom.entity.setting.SettingCategory.THEME);
            settingNew.setTenantId(tenantId);
            list.add(settingNew);
        }
    }

    @GetMapping(value = "/css/theme.css", produces = "text/css")
    @org.springframework.web.bind.annotation.ResponseBody
    public String getThemeCss(
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.onlineStore.admin.security.StoreUserDetails userDetails) {
        ThemeSettingBag themeSettings = service.getThemeSettings();
        List<Setting> listSettings = themeSettings.list();

        // Ensure defaults are present for CSS generation
        if (userDetails != null) {
            checkAndAddDefaults(listSettings, userDetails.getTenantId());
        }

        StringBuilder css = new StringBuilder();
        css.append(":root {\n");

        for (Setting setting : listSettings) {
            String key = setting.getKey();
            String value = setting.getValue();
            String varName = "--theme-" + key.replace("THEME_", "").replace("_", "-").toLowerCase();
            css.append(String.format("    %s: %s;\n", varName, value));

            // Map specific theme vars to Bootstrap for deeper integration
            if ("THEME_COLOR_PRIMARY".equals(key)) {
                css.append(String.format("    --bs-primary: %s;\n", value));
                css.append(String.format("    --bs-link-color: %s;\n", value));
            }
            if ("THEME_COLOR_SECONDARY".equals(key)) {
                css.append(String.format("    --bs-secondary: %s;\n", value));
            }
        }
        css.append("}\n\n");

        // Global Overrides
        css.append(
                ".navbar-top { background-color: var(--theme-header-bg) !important; color: var(--theme-header-color) !important; }\n");
        css.append(
                ".navbar-top .nav-link, .navbar-top .navbar-brand { color: var(--theme-header-color) !important; }\n");
        css.append(".navbar-top { min-height: var(--theme-header-height, 60px) !important; }\n");

        css.append(
                ".navbar-bottom { background-color: var(--theme-footer-bg) !important; color: var(--theme-footer-color) !important; }\n");
        css.append(
                ".navbar-bottom .nav-link, .navbar-bottom .navbar-brand { color: var(--theme-footer-color) !important; }\n");
        css.append(".navbar-bottom { min-height: var(--theme-footer-height, 40px) !important; }\n");

        // Table Overrides
        css.append(
                ".table-header thead th { background-color: var(--theme-table-header-bg) !important; color: var(--theme-table-header-color) !important; }\n");
        css.append(
                ".table-dark { --bs-table-bg: var(--theme-table-header-bg); --bs-table-color: var(--theme-table-header-color); }\n");

        // Dropdown Overrides (Scoped to navbar-top)
        css.append(".navbar-top .dropdown-menu { background-color: var(--theme-header-dropdown-bg) !important; }\n");
        css.append(".navbar-top .dropdown-item { color: var(--theme-header-dropdown-color) !important; }\n");
        css.append(
                ".navbar-top .dropdown-item:hover { background-color: var(--theme-header-dropdown-hover-bg, #6c757d) !important; color: #fff !important; }\n");

        css.append("body { font-family: var(--theme-font-family, sans-serif) !important; }\n");
        css.append("body { font-size: var(--theme-font-size, 14px); }\n");

        // Font Weight handling
        String weight = "normal";
        for (Setting s : listSettings) {
            if ("THEME_FONT_WEIGHT".equals(s.getKey())) {
                weight = s.getValue();
                break;
            }
        }
        css.append(String.format("body { font-weight: %s !important; }\n", weight));

        // Logo Width
        css.append(".theme-logo { width: var(--theme-logo-width, 100px) !important; height: auto !important; }\n");

        return css.toString();
    }
}
