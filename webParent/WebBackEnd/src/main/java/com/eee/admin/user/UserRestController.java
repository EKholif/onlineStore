package com.eee.admin.user;


import com.eee.admin.user.servcies.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserRestController {

    @Autowired
    private UserService userService;

    @PostMapping("/check_email")
    public String checkDuplicateEmail(@Param("id") Long id,@Param("email")  String email) {
        return userService.isEmailUnique(id,email) ?  "OK": "Duplicated"   ;
    }
    }


