package com.onlineStore.admin.setting;

import com.onlineStore.admin.setting.service.SettingService;
import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class BaseThemeController {

    @Autowired
    protected SettingService service;

    protected void checkAndAddDefaults(List<Setting> listSettings, Long tenantId, String prefix,
                                       SettingCategory category) {
        addIfMissing(listSettings, prefix + "_COLOR_PRIMARY", "#007bff", tenantId, category);
        addIfMissing(listSettings, prefix + "_COLOR_SECONDARY", "#6c757d", tenantId, category);
        addIfMissing(listSettings, prefix + "_TABLE_HEADER_BG", "#343a40", tenantId, category);
        addIfMissing(listSettings, prefix + "_TABLE_HEADER_COLOR", "#ffffff", tenantId, category);
        addIfMissing(listSettings, prefix + "_TABLE_ROW_BG", "#ffffff", tenantId, category);
        addIfMissing(listSettings, prefix + "_TABLE_ROW_COLOR", "#212529", tenantId, category);
        addIfMissing(listSettings, prefix + "_TABLE_HOVER_BG", "#f2f2f2", tenantId, category);
        addIfMissing(listSettings, prefix + "_TABLE_BORDER_COLOR", "#dee2e6", tenantId, category);

        // Header & Footer Defaults
        addIfMissing(listSettings, prefix + "_HEADER_BG", "#000000", tenantId, category);
        addIfMissing(listSettings, prefix + "_HEADER_COLOR", "#ffffff", tenantId, category);
        addIfMissing(listSettings, prefix + "_FOOTER_BG", "#343a40", tenantId, category);
        addIfMissing(listSettings, prefix + "_FOOTER_COLOR", "#ffffff", tenantId, category);

        // Header Dropdown Defaults
        addIfMissing(listSettings, prefix + "_HEADER_DROPDOWN_BG", "#343a40", tenantId, category);
        addIfMissing(listSettings, prefix + "_HEADER_DROPDOWN_COLOR", "#ffffff", tenantId, category);
        addIfMissing(listSettings, prefix + "_HEADER_DROPDOWN_HOVER_BG", "#6c757d", tenantId, category);
    }

    protected void addIfMissing(List<Setting> list, String key, String defaultValue, Long tenantId,
                                SettingCategory category) {
        boolean found = false;
        for (Setting s : list) {
            if (s.getKey().equals(key)) {
                found = true;
                break;
            }
        }
        if (!found) {
            Setting setting = new Setting(key, defaultValue, category);
            setting.setTenantId(tenantId);
            list.add(setting);
        }
    }

    protected void saveCommonSettings(List<Setting> listSettings, HttpServletRequest request, Long tenantId,
                                      String prefix, SettingCategory category) {
        // Colors
        saveSetting(listSettings, request, prefix + "_COLOR_PRIMARY", tenantId, category);
        saveSetting(listSettings, request, prefix + "_COLOR_SECONDARY", tenantId, category);
        saveSetting(listSettings, request, prefix + "_TABLE_HEADER_BG", tenantId, category);
        saveSetting(listSettings, request, prefix + "_TABLE_HEADER_COLOR", tenantId, category);
        saveSetting(listSettings, request, prefix + "_TABLE_ROW_BG", tenantId, category);
        saveSetting(listSettings, request, prefix + "_TABLE_ROW_COLOR", tenantId, category);
        saveSetting(listSettings, request, prefix + "_TABLE_HOVER_BG", tenantId, category);
        saveSetting(listSettings, request, prefix + "_TABLE_BORDER_COLOR", tenantId, category);

        saveSetting(listSettings, request, prefix + "_HEADER_BG", tenantId, category);
        saveSetting(listSettings, request, prefix + "_HEADER_COLOR", tenantId, category);
        saveSetting(listSettings, request, prefix + "_FOOTER_BG", tenantId, category);
        saveSetting(listSettings, request, prefix + "_FOOTER_COLOR", tenantId, category);

        saveSetting(listSettings, request, prefix + "_HEADER_DROPDOWN_BG", tenantId, category);
        saveSetting(listSettings, request, prefix + "_HEADER_DROPDOWN_COLOR", tenantId, category);
        saveSetting(listSettings, request, prefix + "_HEADER_DROPDOWN_HOVER_BG", tenantId, category);

        // Dimensions with px suffix
        saveSettingWithSuffix(listSettings, request, prefix + "_HEADER_HEIGHT", "px", tenantId, category);
        saveSettingWithSuffix(listSettings, request, prefix + "_FOOTER_HEIGHT", "px", tenantId, category);
        saveSettingWithSuffix(listSettings, request, prefix + "_LOGO_WIDTH", "px", tenantId, category);
        saveSettingWithSuffix(listSettings, request, prefix + "_FONT_SIZE", "px", tenantId, category);

        // Font Family (Direct save, no suffix) - NOT in checkAndAddDefaults but usually
        // has a default via HTML select
        saveSetting(listSettings, request, prefix + "_FONT_FAMILY", tenantId, category);

        // Font Weight (Checkbox handling)
        String weight = request.getParameter(prefix + "_FONT_WEIGHT");
        updateSettingValue(listSettings, prefix + "_FONT_WEIGHT", weight == null ? "normal" : weight, tenantId,
                category);
    }

    protected void saveSetting(List<Setting> list, HttpServletRequest request, String key, Long tenantId,
                               SettingCategory category) {
        String value = request.getParameter(key);
        if (value != null) {
            updateSettingValue(list, key, value, tenantId, category);
        }
    }

    protected void saveSettingWithSuffix(List<Setting> list, HttpServletRequest request, String key, String suffix,
                                         Long tenantId, SettingCategory category) {
        String value = request.getParameter(key);
        if (value != null && !value.isEmpty()) {
            updateSettingValue(list, key, value + suffix, tenantId, category);
        }
    }

    protected void updateSettingValue(List<Setting> list, String key, String value, Long tenantId,
                                      SettingCategory category) {
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
            Setting settingNew = new Setting(key, value, category);
            settingNew.setTenantId(tenantId);
            list.add(settingNew);
        }
    }

    protected String generateCommonCss(List<Setting> listSettings, String prefix) {
        StringBuilder css = new StringBuilder();
        css.append(":root {\n");

        for (Setting setting : listSettings) {
            String key = setting.getKey();
            String value = setting.getValue();
            if (key.startsWith(prefix)) {
                // Generate standardized CSS variable name (e.g., --theme-color-primary)

                String keyWithoutPrefix = key.replaceFirst(prefix + "_", "");
                String varName = "--theme-" + keyWithoutPrefix.replace("_", "-").toLowerCase();

                css.append(String.format("    %s: %s;\n", varName, value));

                // Bootstrap overrides for PRIMARY and SECONDARY
                if ((prefix + "_COLOR_PRIMARY").equals(key)) {
                    css.append(String.format("    --bs-primary: %s;\n", value));
                    css.append(String.format("    --bs-link-color: %s;\n", value));
                }
                if ((prefix + "_COLOR_SECONDARY").equals(key)) {
                    css.append(String.format("    --bs-secondary: %s;\n", value));
                }
            }
        }
        css.append("}\n\n");
        return css.toString();
    }
}
