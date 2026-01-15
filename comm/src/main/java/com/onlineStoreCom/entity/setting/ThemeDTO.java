package com.onlineStoreCom.entity.setting;

public class ThemeDTO {
    private String primaryColor;
    private String secondaryColor;
    private String headerBg;
    private String headerColor;
    private String footerBg;
    private String footerColor;
    private String fontFamily;
    private String fontSize;
    private String fontWeight;
    // Add raw map for flexibility or other fields as needed

    public ThemeDTO() {
    }

    public ThemeDTO(String primaryColor, String secondaryColor, String headerBg, String headerColor,
                    String footerBg, String footerColor, String fontFamily, String fontSize, String fontWeight) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.headerBg = headerBg;
        this.headerColor = headerColor;
        this.footerBg = footerBg;
        this.footerColor = footerColor;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.fontWeight = fontWeight;
    }

    // Getters and Setters
    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public String getHeaderBg() {
        return headerBg;
    }

    public void setHeaderBg(String headerBg) {
        this.headerBg = headerBg;
    }

    public String getHeaderColor() {
        return headerColor;
    }

    public void setHeaderColor(String headerColor) {
        this.headerColor = headerColor;
    }

    public String getFooterBg() {
        return footerBg;
    }

    public void setFooterBg(String footerBg) {
        this.footerBg = footerBg;
    }

    public String getFooterColor() {
        return footerColor;
    }

    public void setFooterColor(String footerColor) {
        this.footerColor = footerColor;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }
}
