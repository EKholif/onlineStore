package com.onlineStore.admin;

import com.onlineStore.admin.usersAndCustomers.users.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Main controller for handling root and authentication-related requests.
 * <p>
 * This controller manages the main entry points of the application including
 * the home page and login page. It handles redirections and authentication
 * checks.
 * 
 * @Controller - Marks this class as a Spring MVC controller
 */
@Controller
public class MainController {

    @Autowired
    private RoleRepository repo;

    /**
     * Handles requests to the root URL (/) and redirects to the users page.
     * 
     * @return String representing the redirect path to "/users/users"
     */
    @GetMapping("/")
    public String ViewHome() {
        return "redirect:/users/users";
    }

    /**
     * Handles requests to the "/ind" URL and returns the index page.
     * 
     * @return String representing the view name "index"
     */
    @GetMapping("/ind")
    public String View() {
        return "/index";
    }

    /**
     * Handles requests to the login page.
     * <p>
     * If the user is already authenticated, redirects to the home page.
     * Otherwise, displays the login page.
     * 
     * @return String representing the view name "login" or a redirect to home
     */
    @GetMapping("/login")
    public String viewLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("MainController.viewLoginPage called. Auth: " + authentication);
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            System.out.println("MainController: returning login view");
            return "login";
        }
        System.out.println("MainController: redirecting to /");
        return "redirect:/";
    }
}
