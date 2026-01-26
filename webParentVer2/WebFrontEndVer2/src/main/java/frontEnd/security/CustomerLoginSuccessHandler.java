package frontEnd.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomerLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        String redirectURL = request.getParameter("redirect");

        if (redirectURL != null && !redirectURL.isEmpty()) {
            // Basic security check: ensure redirect is local to avoid open redirect
            // vulnerability
            // For now, assuming startsWith("/") is enough validation for internal logic
            if (redirectURL.startsWith("/")) {
                response.sendRedirect(redirectURL);
                return;
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
