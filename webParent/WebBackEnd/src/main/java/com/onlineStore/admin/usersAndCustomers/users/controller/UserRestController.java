package com.onlineStore.admin.usersAndCustomers.users.controller;


import com.onlineStore.admin.usersAndCustomers.users.servcies.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserRestController {

    @Autowired
    private UserService userService;

    @PostMapping("/check_email")
    public String checkDuplicateEmail(@Param("id") Long id, @Param("email") String email) {
        return userService.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}


