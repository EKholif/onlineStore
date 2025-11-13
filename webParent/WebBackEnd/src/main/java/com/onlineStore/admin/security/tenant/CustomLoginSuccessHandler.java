package com.onlineStore.admin.security.tenant;

import com.onlineStoreCom.entity.users.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    // Key used to store the Tenant ID in the HttpSession
    private static final String SESSION_TENANT_ID_KEY = "TENANT_ID";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // 1. Extract the authenticated Principal (User object)
        Object principal = authentication.getPrincipal();

        // 2. Safely cast and extract the Long Tenant ID
        if (principal instanceof User) {
            User user = (User) principal;
            Long tenantId = user.getTenantId(); // Assumes User entity has getTenantId() method

            if (tenantId != null) {
                // 3. Store the Long Tenant ID in the HTTP Session
                HttpSession session = request.getSession(true); // Get or create session
                session.setAttribute(SESSION_TENANT_ID_KEY, tenantId);


                // OPTIONAL: Set the context for immediate use in the current request
                TenantContext.setTenantId(tenantId);
            }

            System.out.println("Login Success: User '" + user.getEmail() + "' with Tenant ID: " + tenantId);
        }

        // 4. Redirect the user to the home page or dashboard
        response.sendRedirect(request.getContextPath() + "/");
    }
}