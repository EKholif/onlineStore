package com.onlineStore.admin.security.tenant;

import com.onlineStoreCom.security.tenant.TenantContextFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterRegistrationConfig {

    @Autowired
    private TenantContextFilter tenantContextFilter;

    /**
     * Disable auto-registration of TenantContextFilter in the global chain.
     * We manually register it in the Security Filter Chain.
     */
    @Bean
    public FilterRegistrationBean<TenantContextFilter> tenantContextFilterRegistration() {
        FilterRegistrationBean<TenantContextFilter> registration = new FilterRegistrationBean<>(tenantContextFilter);
        registration.setEnabled(false);
        return registration;
    }
}
