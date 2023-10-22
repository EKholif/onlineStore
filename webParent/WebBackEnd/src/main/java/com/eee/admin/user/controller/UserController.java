package com.eee.admin.user.controller;


import com.eee.admin.user.UserRepository;
import com.eee.admin.user.servcies.UserService;
import com.eee.common.entity.Role;
import com.eee.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/users")
    public ModelAndView listAllUsers( ){
        ModelAndView model = new ModelAndView("users");
         List<User> list = userService.listAllUsers();
         model.addObject("users", list);
         return model;
     }

    @GetMapping("/newUserForm")
    public ModelAndView newUserForm() {
        ModelAndView model = new ModelAndView("newUserForm");
       List<Role> listAllRoles = userService.listAllRoles();
        User newUser = new User();
        newUser.setEnable(true);
        model.addObject("user", newUser);
        model.addObject("listAllRoles", listAllRoles);

         return model;

    }

    @PostMapping({ "/save-user"} )
    public ModelAndView saveNewUser( @ModelAttribute final User user, RedirectAttributes redirectAttributes ) {

        redirectAttributes.addFlashAttribute("message", "the user has been saved");
        userService.saveUser(user);
        return new ModelAndView("redirect:/users");

    }


 }
