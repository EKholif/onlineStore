package com.example.fontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManinController {

    @GetMapping("/")
    public String ViewMain(){
        return ("index");
    }
}
