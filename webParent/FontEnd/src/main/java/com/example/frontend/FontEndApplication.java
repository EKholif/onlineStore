package com.example.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.onlineStoreCom.entity")
public class FontEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(FontEndApplication.class, args);
    }

}
