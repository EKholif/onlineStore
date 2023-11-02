package com.onlineStore.admin;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.onlineStore.entity","com.onlineStore.admin.role"})
public class WebBackEndApplication {


    public static void main(String[] args) {
        SpringApplication.run(WebBackEndApplication.class, args);


    }
}