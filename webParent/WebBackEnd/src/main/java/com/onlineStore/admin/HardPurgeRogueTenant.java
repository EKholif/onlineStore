package com.onlineStore.admin;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1) // Run VERY first
public class HardPurgeRogueTenant implements CommandLineRunner {

    @Autowired
    private EntityManager entityManager;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("💀 HARD PURGE SKIPPED (To allow Asset Migration)");
    }
}
