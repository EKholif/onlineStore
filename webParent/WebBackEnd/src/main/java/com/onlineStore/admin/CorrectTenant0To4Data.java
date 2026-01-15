package com.onlineStore.admin;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * [AG-DATA-FIX-005] Migrate Tenant 0 (System/Default) Data to Tenant 4
 * <p>
 * WHY: TenantContextFilter defaulted to 0 when no tenant was found.
 * Users might have created data under Tenant 0 that belongs to Tenant 4.
 * STRATEGY: MOVE all data from 0 -> 4. Handle collisions for Settings.
 */
@Component
@Order(1)
public class CorrectTenant0To4Data implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorrectTenant0To4Data.class);

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        LOGGER.info("================================================================================");
        LOGGER.info("🔧 MIGRATING DATA: TENANT 0 -> TENANT 4 (SKIPPED FOR ASSET MIGRATION)");
        LOGGER.info("================================================================================");
    }
}
