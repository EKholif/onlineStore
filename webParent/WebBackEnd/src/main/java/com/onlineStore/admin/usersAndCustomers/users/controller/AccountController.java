package com.onlineStore.admin.usersAndCustomers.users.controller;

import com.onlineStore.admin.usersAndCustomers.users.servcies.UserService;
import com.onlineStore.admin.UsernameNotFoundException;
import com.onlineStore.admin.product.security.StoreUserDetails;
import com.onlineStoreCom.entity.users.Role;
import com.onlineStoreCom.entity.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
public class AccountController {

    @Autowired
    private UserDetailsService service;

    @Autowired
    private UserService userService;

    @GetMapping("/account")
    public ModelAndView ViewAccountDetails(@AuthenticationPrincipal StoreUserDetails loggedUser,
                                           RedirectAttributes redirectAttributes) {

        Long id = loggedUser.getId();
        ModelAndView model = new ModelAndView("users/new-users-form");

        try {
            User user = userService.getUser(id);
            List<Role> listAllRoles = userService.listAllRoles();
            model.addObject("user", user);
            model.addObject("listAllRoles", user.getRoles());
            model.addObject("pageTitle", "Edit User " + user.getfirstName() + " (ID: " + id + ")");
            model.addObject("listAllRoles", listAllRoles);
            model.addObject("saveChanges", "/save-edit-user/");
            model.addObject("id", id);
            return model;

        } catch (UsernameNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return new ModelAndView("redirect:/users");


        }
    }


}






