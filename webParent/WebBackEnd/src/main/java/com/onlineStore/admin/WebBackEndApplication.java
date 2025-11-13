package com.onlineStore.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Main entry point for the Online Store Admin Backend application.
 * <p>
 * This class bootstraps the Spring Boot application and configures component scanning.
 * It enables auto-configuration and component scanning for the specified packages.
 * 
 * @SpringBootApplication - Enables Spring Boot's auto-configuration, component scanning,
 *                        and additional configuration capabilities.
 * @EntityScan - Specifies the base package to scan for JPA entities.
 */
@SpringBootApplication
@EntityScan({"com.onlineStoreCom.entity"})
public class WebBackEndApplication {

    /**
     * Main method that serves as the entry point for the application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(WebBackEndApplication.class, args);
    }
}