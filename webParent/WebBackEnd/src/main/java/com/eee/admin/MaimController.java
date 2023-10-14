package com.eee.admin;

import com.eee.admin.role.RoleRepository;
import com.eee.common.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MaimController {

    @Autowired

    private  RoleRepository repo;



    @GetMapping("/")
    public String ViewHome(){
        Role role = new Role("appppppppp", "been");
        Role savedRole = repo.save(role);
        return "index";
    }
}
