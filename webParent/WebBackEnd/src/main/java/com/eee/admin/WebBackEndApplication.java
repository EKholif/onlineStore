package com.eee.admin;


import com.eee.admin.role.RoleRepository;
import com.eee.common.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan({"com.eee.common.entity","com.eee.admin.role"})
public class WebBackEndApplication {


    public static void main(String[] args) {
        SpringApplication.run(WebBackEndApplication.class, args);


    }
}