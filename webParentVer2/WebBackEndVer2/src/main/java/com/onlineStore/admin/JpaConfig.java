package com.onlineStore.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = { "com.onlineStore.admin", "com.onlineStore.services",
        "com.onlineStoreCom.repo" })
@EnableTransactionManagement
public class JpaConfig {
}
