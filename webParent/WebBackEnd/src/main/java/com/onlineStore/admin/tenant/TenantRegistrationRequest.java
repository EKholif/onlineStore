package com.onlineStore.admin.tenant;

public class TenantRegistrationRequest {
    private String name;
    private String code;
    private String adminEmail;
    private String adminPassword;

    public TenantRegistrationRequest() {
    }

    public TenantRegistrationRequest(String name, String code, String adminEmail, String adminPassword) {
        this.name = name;
        this.code = code;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
