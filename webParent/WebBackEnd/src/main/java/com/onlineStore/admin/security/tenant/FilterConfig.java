package com.onlineStore.admin.security.tenant;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<TenantContextFilter> tenantContextFilter(EntityManagerFactory emf) {
        FilterRegistrationBean<TenantContextFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TenantContextFilter(emf));
        registrationBean.addUrlPatterns("/*"); // أو حسب المسارات اللي عايز الفلتر يشتغل عليها
        return registrationBean;
    }
}
