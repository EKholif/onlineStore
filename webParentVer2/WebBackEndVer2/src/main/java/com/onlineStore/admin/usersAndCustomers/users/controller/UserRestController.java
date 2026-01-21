package com.onlineStore.admin.usersAndCustomers.users.controller;


import com.onlineStore.admin.usersAndCustomers.users.servcies.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

    @Autowired
    private UserService userService;

    @PostMapping("/check_email")
    public String checkDuplicateEmail( Integer id,  String email) {
        return userService.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}


