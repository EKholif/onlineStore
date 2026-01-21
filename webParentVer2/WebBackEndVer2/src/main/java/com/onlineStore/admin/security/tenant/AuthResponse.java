package com.onlineStore.admin.security.tenant;

public class AuthResponse {
    private String token;
    private Long tenantId;

    public AuthResponse() {}

    public AuthResponse(String token, Long tenantId) {
        this.token = token;
        this.tenantId = tenantId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
}