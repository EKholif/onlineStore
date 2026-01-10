package com.onlineStoreCom.security.tenant;

import jakarta.servlet.http.HttpServletRequest;

public interface TenantJwtHelper {
    Long extractTenantIdFromRequest(HttpServletRequest request);
}
