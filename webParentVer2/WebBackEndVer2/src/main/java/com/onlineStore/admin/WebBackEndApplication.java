package com.onlineStore.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Online Store Admin Backend application.
 */
@SpringBootApplication(scanBasePackages = { "com.onlineStore.admin", "com.onlineStore.services", "com.onlineStoreCom" })
@org.springframework.boot.autoconfigure.domain.EntityScan({ "com.onlineStoreCom.entity" })
@org.springframework.data.jpa.repository.config.EnableJpaRepositories(basePackages = {
        "com.onlineStore.admin",
        "com.onlineStore.services",
        "com.onlineStoreCom.repo",
        "com.onlineStoreCom.analytics"
})
public class WebBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebBackEndApplication.class, args);
    }
}