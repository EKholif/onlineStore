package com.onlineStore.admin.security.tenant;

import com.onlineStore.admin.security.StoreUserDetails;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private com.onlineStore.admin.security.jwt.JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        Object principal = authentication.getPrincipal();

        if (principal instanceof StoreUserDetails) {
            StoreUserDetails user = (StoreUserDetails) principal;
            Long tenantId = user.getTenantId();

            System.out.println("🔐 [CustomLoginSuccessHandler] Login Success!");
            System.out.println("   > User: " + user.getUsername());
            System.out.println("   > TenantID from Details: " + tenantId);

            // 1. Save tenant id in session (Legacy Support)
            HttpSession session = request.getSession(true);
            session.setAttribute("TENANT_ID", tenantId);

            // 2. Set ThreadLocal
            TenantContext.setTenantId(tenantId);

            // 3. [NEW] Generate JWT with TenantID
            String token = jwtUtils.generateToken(user.getUsername(), tenantId);

            // 4. Attach as HttpOnly Cookie (Secure & Accessible to Backend)
            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("ACCESS_TOKEN", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(10 * 60 * 60); // 10 Hours
            response.addCookie(cookie);

            System.out.println("   > JWT Generated & Cookie Set: " + token.substring(0, 15) + "...");
        }

        // Redirect to home
        response.sendRedirect("/");
    }
}
