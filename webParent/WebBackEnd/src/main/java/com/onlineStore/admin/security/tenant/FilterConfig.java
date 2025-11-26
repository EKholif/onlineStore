package com.onlineStore.admin.security.tenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private final TenantContextFilter tenantContextFilter;

    public FilterConfig(TenantContextFilter tenantContextFilter) {
        this.tenantContextFilter = tenantContextFilter;
    }

    @Bean
    public FilterRegistrationBean<TenantContextFilter> tenantFilterRegistration() {
        FilterRegistrationBean<TenantContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(tenantContextFilter);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
