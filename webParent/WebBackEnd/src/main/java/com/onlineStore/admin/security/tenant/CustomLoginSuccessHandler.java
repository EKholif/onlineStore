package com.onlineStore.admin.security.tenant;

import com.onlineStore.admin.security.StoreUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        Object principal = authentication.getPrincipal();

        if (principal instanceof StoreUserDetails) {
            StoreUserDetails user = (StoreUserDetails) principal;
            Long tenantId = user.getTenantId();

            // Save tenant id in session for subsequent requests
            HttpSession session = request.getSession(true);
            session.setAttribute("TENANT_ID", tenantId);

            // Also set ThreadLocal for the current request thread (optional, useful if filter runs in same thread)
            TenantContext.setTenantId(tenantId);
        }

        // Redirect to home (or any post-login page)
        response.sendRedirect("/");
    }
}
