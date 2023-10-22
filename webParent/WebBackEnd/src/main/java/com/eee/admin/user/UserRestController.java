package com.eee.admin.user;


import com.eee.admin.user.servcies.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserRestController {

    @Autowired
    private UserService userService;

    @PostMapping("/check_email")
    public String checkDuplicateEmail(String email) {
        return userService.isEmailUnique(email) ?  "Duplicated" : "OK"  ;
    }
    }


