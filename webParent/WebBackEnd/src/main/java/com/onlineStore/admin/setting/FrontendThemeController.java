package com.onlineStore.admin.setting;

import com.onlineStore.admin.setting.settingBag.ThemeSettingBag;
import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class FrontendThemeController extends BaseThemeController {

    private static final String PREFIX = "FRONTEND_THEME";
    private static final SettingCategory CATEGORY = SettingCategory.FRONTEND_THEME;

    @GetMapping("/settings/frontend-theme")
    public String viewThemeSettings(Model model,
                                    @org.springframework.security.core.annotation.AuthenticationPrincipal com.onlineStore.admin.security.StoreUserDetails userDetails) {
        ThemeSettingBag themeSettings = service.getFrontendThemeSettings();
        List<Setting> listSettings = themeSettings.list();

        checkAndAddDefaults(listSettings, userDetails.getTenantId(), PREFIX, CATEGORY);

        for (Setting setting : listSettings) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }

        return "settings/frontend_theme_settings";
    }

    @PostMapping("/settings/frontend-theme/save")
    public String saveThemeSettings(HttpServletRequest request, RedirectAttributes ra,
                                    @org.springframework.security.core.annotation.AuthenticationPrincipal com.onlineStore.admin.security.StoreUserDetails userDetails) {
        ThemeSettingBag themeSettings = service.getFrontendThemeSettings();
        List<Setting> listSettings = themeSettings.list();
        Long tenantId = userDetails.getTenantId();

        saveCommonSettings(listSettings, request, tenantId, PREFIX, CATEGORY);

        service.saveAll(listSettings);
        ra.addFlashAttribute("message", "Frontend Theme settings have been saved.");

        return "redirect:/settings/frontend-theme";
    }

    @GetMapping(value = "/css/frontend_theme.css", produces = "text/css")
    @org.springframework.web.bind.annotation.ResponseBody
    public String getThemeCss(
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.onlineStore.admin.security.StoreUserDetails userDetails) {
        ThemeSettingBag themeSettings = service.getFrontendThemeSettings();
        List<Setting> listSettings = themeSettings.list();

        if (userDetails != null) {
            checkAndAddDefaults(listSettings, userDetails.getTenantId(), PREFIX, CATEGORY);
        }

        StringBuilder css = new StringBuilder();
        css.append(generateCommonCss(listSettings, PREFIX));

        // Replicate Admin overrides for now, assuming the preview HTML is identical
        // and the Frontend might eventually use similar classes or we update this
        // later.

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
        css.append(
                ".table { --bs-table-color: var(--theme-table-row-color); --bs-table-bg: var(--theme-table-row-bg); --bs-table-border-color: var(--theme-table-border-color); --bs-table-hover-bg: var(--theme-table-hover-bg); }\n");

        // Dropdown Overrides API
        css.append(".navbar-top .dropdown-menu { background-color: var(--theme-header-dropdown-bg) !important; }\n");
        css.append(
                ".navbar-top .dropdown-item, .navbar-top .dropdown-menu .nav-link { color: var(--theme-header-dropdown-color) !important; }\n");
        css.append(
                ".navbar-top .dropdown-item:hover, .navbar-top .dropdown-menu .nav-link:hover { background-color: var(--theme-header-dropdown-hover-bg, #6c757d) !important; color: #fff !important; }\n");

        css.append("body { font-family: var(--theme-font-family, sans-serif) !important; }\n");
        css.append("body { font-size: var(--theme-font-size, 14px); }\n");

        String weight = "normal";
        for (Setting s : listSettings) {
            if ((PREFIX + "_FONT_WEIGHT").equals(s.getKey())) {
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
