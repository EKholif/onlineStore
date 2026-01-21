package com.onlineStore.admin.security.jwt;

import com.onlineStoreCom.security.tenant.TenantJwtHelper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantJwtHelperImpl implements TenantJwtHelper {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Long extractTenantIdFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    return jwtUtils.extractTenantId(token);
                }
            }
        }
        return null;
    }
}
