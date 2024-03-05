package com.onlineStore.admin;

import com.onlineStore.admin.user.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired

    private  RoleRepository repo;



    @GetMapping("/")
    public String ViewHome(){
        return "index";
    }

    @GetMapping("/login")
    public String signIn(){
        return "login";

    }
    @GetMapping("/ldn")
    public String neds(){
        return "losssgin";

    }

//    @GetMapping("/error")
//    public String tneds(){
//        return "error";
//
//    }
}



