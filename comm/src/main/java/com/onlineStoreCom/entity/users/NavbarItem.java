package com.onlineStoreCom.entity.users;


public class NavbarItem {
        private String url;
        private String label;

    public NavbarItem(String url, String label) {
        this.url = url;
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
