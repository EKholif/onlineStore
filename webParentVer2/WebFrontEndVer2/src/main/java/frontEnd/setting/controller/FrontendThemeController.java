package frontEnd.setting.controller;

import com.onlineStoreCom.entity.setting.Setting;
import frontEnd.setting.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class FrontendThemeController {

    private static final String PREFIX = "FRONTEND_THEME";
    @Autowired
    private SettingService service;

    @GetMapping(value = "/css/frontend_theme.css", produces = "text/css")
    @ResponseBody
    public String getThemeCss() {
        Long tenantId = com.onlineStoreCom.tenant.TenantContext.getTenantId();
        System.out.println("🎨 [FrontendThemeController] Requesting CSS for Tenant ID: " + tenantId);

        List<Setting> listSettings = service.getFrontendThemeSettings();
        System.out.println("🎨 [FrontendThemeController] Found settings count: "
                + (listSettings != null ? listSettings.size() : "null"));

        return generateCommonCss(listSettings, PREFIX);
    }

    // Copied from BaseThemeController (since we can't easily share it across
    // modules without Common)
    protected String generateCommonCss(List<Setting> listSettings, String prefix) {
        StringBuilder css = new StringBuilder();
        css.append(":root {\n");

        for (Setting setting : listSettings) {
            String key = setting.getKey();
            String value = setting.getValue();

            // Colors
            if (key.startsWith(prefix + "_COLOR_")) {
                String varName = "--theme-" + key.replace(prefix + "_COLOR_", "").toLowerCase().replace("_", "-");
                css.append(String.format("    %s: %s;\n", varName, value));
            }
            // Header
            else if (key.startsWith(prefix + "_HEADER_")) {
                String suffix = key.replace(prefix + "_HEADER_", "").toLowerCase().replace("_", "-");
                String varName = "--theme-header-" + suffix;
                // Add px if height
                if (suffix.contains("height") && !value.endsWith("px") && !value.equals("auto")) {
                    value += "px";
                }
                css.append(String.format("    %s: %s;\n", varName, value));
            }
            // Footer
            else if (key.startsWith(prefix + "_FOOTER_")) {
                String suffix = key.replace(prefix + "_FOOTER_", "").toLowerCase().replace("_", "-");
                String varName = "--theme-footer-" + suffix;
                if (suffix.contains("height") && !value.endsWith("px") && !value.equals("auto")) {
                    value += "px";
                }
                css.append(String.format("    %s: %s;\n", varName, value));
            }
            // Table
            else if (key.startsWith(prefix + "_TABLE_")) {
                String suffix = key.replace(prefix + "_TABLE_", "").toLowerCase().replace("_", "-");
                String varName = "--theme-table-" + suffix;
                css.append(String.format("    %s: %s;\n", varName, value));
            }
            // Logo
            else if (key.startsWith(prefix + "_LOGO_")) {
                String suffix = key.replace(prefix + "_LOGO_", "").toLowerCase().replace("_", "-");
                String varName = "--theme-logo-" + suffix;
                if (suffix.contains("width") && !value.endsWith("px") && !value.equals("auto")) {
                    value += "px";
                }
                css.append(String.format("    %s: %s;\n", varName, value));
            }
            // Typography
            else if (key.startsWith(prefix + "_FONT_")) {
                String suffix = key.replace(prefix + "_FONT_", "").toLowerCase().replace("_", "-");
                String varName = "--theme-font-" + suffix;
                if (suffix.equals("size") && !value.endsWith("px")) {
                    value += "px";
                }
                css.append(String.format("    %s: %s;\n", varName, value));
            }
        }

        css.append("}\n");

        // Add override classes to ensure application
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

        // Font Weight Logic
        String weight = "normal";
        for (Setting s : listSettings) {
            if ((PREFIX + "_FONT_WEIGHT").equals(s.getKey())) {
                weight = s.getValue();
                break;
            }
        }
        css.append(String.format("body { font-weight: %s !important; }\n", weight));

        return css.toString();
    }
}
