package com.onlineStore.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Online Store Admin Backend application.
 */
@SpringBootApplication(scanBasePackages = {"com.onlineStore.admin", "com.onlineStore.services"})
@org.springframework.boot.autoconfigure.domain.EntityScan({"com.onlineStoreCom.entity"})
public class WebBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebBackEndApplication.class, args);
    }
}