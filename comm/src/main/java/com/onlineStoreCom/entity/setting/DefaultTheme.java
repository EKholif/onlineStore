package com.onlineStoreCom.entity.setting;

import java.util.HashMap;
import java.util.Map;

public class DefaultTheme {

    public static final Map<String, String> SETTINGS = new HashMap<>();

    static {
        // Colors
        SETTINGS.put("THEME_COLOR_PRIMARY", "#007bff");
        SETTINGS.put("THEME_COLOR_SECONDARY", "#6c757d");

        // Header & Footer
        SETTINGS.put("THEME_HEADER_BG", "#000000");
        SETTINGS.put("THEME_HEADER_COLOR", "#ffffff");
        SETTINGS.put("THEME_FOOTER_BG", "#343a40");
        SETTINGS.put("THEME_FOOTER_COLOR", "#ffffff");

        // Table
        SETTINGS.put("THEME_TABLE_HEADER_BG", "#343a40");
        SETTINGS.put("THEME_TABLE_HEADER_COLOR", "#ffffff");

        // Dropdown
        SETTINGS.put("THEME_HEADER_DROPDOWN_BG", "#343a40");
        SETTINGS.put("THEME_HEADER_DROPDOWN_COLOR", "#ffffff");
        SETTINGS.put("THEME_HEADER_DROPDOWN_HOVER_BG", "#6c757d");

        // Dimensions
        SETTINGS.put("THEME_HEADER_HEIGHT", "60px");
        SETTINGS.put("THEME_FOOTER_HEIGHT", "40px");
        SETTINGS.put("THEME_LOGO_WIDTH", "100px");

        // Typography
        SETTINGS.put("THEME_FONT_FAMILY", "'Inter', sans-serif");
        SETTINGS.put("THEME_FONT_SIZE", "14px");
        SETTINGS.put("THEME_FONT_WEIGHT", "normal");
    }
}
