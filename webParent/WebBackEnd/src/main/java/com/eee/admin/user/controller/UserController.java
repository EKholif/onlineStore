package com.eee.admin.user.controller;


import com.eee.admin.UsernameNotFoundException;
import com.eee.admin.role.RoleRepository;
import com.eee.admin.user.UserRepository;
import com.eee.admin.user.servcies.UserService;
import com.eee.common.entity.Role;
import com.eee.common.entity.User;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    @GetMapping("/users")
    public ModelAndView listAllUsers() {
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
        model.addObject("pageTitle","Creat new User" );
        model.addObject("saveChanges", "/save-user");
        model.addObject("UserId", 0L);

        return model;

    }

    @PostMapping("/save-user")
    public ModelAndView saveNewUser(@ModelAttribute  User user, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "the user has been saved");
        userService.saveUser(user);
        return new ModelAndView("redirect:/users");
    }



    @GetMapping("/edit/{id}")
    public ModelAndView editUser(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {


        ModelAndView model = new ModelAndView("newUserForm");
        try {
            User user = userService.getUser(id);
            List<Role> listAllRoles = userService.listAllRoles();
            model.addObject("user", user);
            model.addObject("listAllRoles",  user.getRoles()  ) ;
            model.addObject("pageTitle","Edit User (ID: " + id + ")" );
            model.addObject("listAllRoles", listAllRoles);
            model.addObject("saveChanges", "/save-edit-user/"+id);
            model.addObject("UserId", id);
            return model;

        } catch (UsernameNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message  Ehab", ex.getMessage());
            return new ModelAndView("redirect:/users");

        }


    }
    @PostMapping( "/save-edit-user/{id}")
    public ModelAndView saveUpdaterUser(@PathVariable Long id,@ModelAttribute  User user, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "the user has been updated");
        User updateUser =userService.findUserById(id);
        BeanUtils.copyProperties(user, updateUser, "id");
          userService.saveUser(updateUser);
        return new ModelAndView("redirect:/users");
    }


    @GetMapping (  "/delUser/{id}")
        public ModelAndView deleteUser(@PathVariable (name = "id")Long id, RedirectAttributes redirectAttributes) {

        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "User Deleted");
            return new ModelAndView("redirect:/users");
        } catch (UsernameNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return new ModelAndView("redirect:/users");
        }
       }
}
