package com.eee.admin.user.controller;


import com.eee.admin.user.UserRepository;
import com.eee.admin.user.servcies.UserService;
import com.eee.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService service;


    @GetMapping("/newUserForm")
    public Model newUser(Model model ){
         List<User> list = service.listAll();
         model.addAttribute("users", list);
         return model;
     }

    @GetMapping("/users")
    public Model newUserForm(Model muv) {
        List<User> list = service.listAll();
        muv.addAttribute("users", list);
        return muv;
    }






 }
