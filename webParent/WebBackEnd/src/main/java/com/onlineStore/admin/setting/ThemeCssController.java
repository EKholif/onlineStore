package com.onlineStore.admin.setting;

import com.onlineStore.admin.setting.service.SettingService;
import com.onlineStore.admin.setting.settingBag.ThemeSettingBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ThemeCssController {

    @Autowired
    private SettingService service;

    @GetMapping(value = "/css/theme.css", produces = "text/css")
    @ResponseBody
    public String getThemeCss() {
        ThemeSettingBag themeSettings = service.getThemeSettings();

        // Defaults (could be moved to a simpler structure or database check)
        String primaryColor = themeSettings.getValue("THEME_COLOR_PRIMARY");
        if (primaryColor == null)
            primaryColor = "#007bff"; // Default Blue

        String secondaryColor = themeSettings.getValue("THEME_COLOR_SECONDARY");
        if (secondaryColor == null)
            secondaryColor = "#6c757d"; // Default Grey

        String tableHeaderBg = themeSettings.getValue("THEME_TABLE_HEADER_BG");
        if (tableHeaderBg == null)
            tableHeaderBg = "#343a40"; // Default Dark

        String tableHeaderColor = themeSettings.getValue("THEME_TABLE_HEADER_COLOR");
        if (tableHeaderColor == null)
            tableHeaderColor = "#ffffff"; // Default White

        String headerBg = themeSettings.getValue("THEME_HEADER_BG");
        if (headerBg == null)
            headerBg = "#000000";

        String headerColor = themeSettings.getValue("THEME_HEADER_COLOR");
        if (headerColor == null)
            headerColor = "#ffffff";

        String footerBg = themeSettings.getValue("THEME_FOOTER_BG");
        if (footerBg == null)
            footerBg = "#343a40";

        String footerColor = themeSettings.getValue("THEME_FOOTER_COLOR");
        if (footerColor == null)
            footerColor = "#ffffff";

        String dropdownBg = themeSettings.getValue("THEME_HEADER_DROPDOWN_BG");
        if (dropdownBg == null)
            dropdownBg = "#343a40";

        String dropdownColor = themeSettings.getValue("THEME_HEADER_DROPDOWN_COLOR");
        if (dropdownColor == null)
            dropdownColor = "#ffffff";

        StringBuilder css = new StringBuilder();
        css.append(":root {\n");
        css.append("    --theme-primary: ").append(primaryColor).append(";\n");
        css.append("    --theme-secondary: ").append(secondaryColor).append(";\n");
        css.append("    --theme-table-header-bg: ").append(tableHeaderBg).append(";\n");
        css.append("    --theme-table-header-color: ").append(tableHeaderColor).append(";\n");
        css.append("    --theme-header-bg: ").append(headerBg).append(";\n");
        css.append("    --theme-header-color: ").append(headerColor).append(";\n");
        css.append("    --theme-footer-bg: ").append(footerBg).append(";\n");
        css.append("    --theme-footer-color: ").append(footerColor).append(";\n");
        css.append("    --theme-header-dropdown-bg: ").append(dropdownBg).append(";\n");
        css.append("    --theme-header-dropdown-color: ").append(dropdownColor).append(";\n");

        // Bootstrap Overrides
        css.append("    --bs-primary: ").append(primaryColor).append(";\n");
        css.append("    --bs-secondary: ").append(secondaryColor).append(";\n");
        css.append("    --bs-link-color: ").append(primaryColor).append(";\n");
        css.append("}\n\n");

        // Table Overrides
        css.append(
                ".table-header thead th { background-color: var(--theme-table-header-bg) !important; color: var(--theme-table-header-color) !important; }\n");
        css.append(
                ".table-dark { --bs-table-bg: var(--theme-table-header-bg); --bs-table-color: var(--theme-table-header-color); }\n");

        // Header & Footer Overrides
        css.append(".navbar-top { background-color: var(--theme-header-bg) !important; }\n");
        css.append(
                ".navbar-top .nav-link, .navbar-top .navbar-brand { color: var(--theme-header-color) !important; }\n");

        css.append(".navbar-bottom { background-color: var(--theme-footer-bg) !important; }\n");
        css.append(
                ".navbar-bottom .nav-link, .navbar-bottom .navbar-brand { color: var(--theme-footer-color) !important; }\n");

        // Header Dropdown Overrides (Scoped to navbar-top to avoid affecting other
        // dropdowns if any)
        css.append(".navbar-top .dropdown-menu { background-color: var(--theme-header-dropdown-bg) !important; }\n");
        css.append(".navbar-top .dropdown-item { color: var(--theme-header-dropdown-color) !important; }\n");
        css.append(
                ".navbar-top .dropdown-item:hover { background-color: var(--theme-secondary) !important; color: #fff !important; }\n"); // Suggest
        // nice
        // hover
        // effect

        // Add more dynamic styles as needed

        return css.toString();
    }
}
