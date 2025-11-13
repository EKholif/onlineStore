package com.onlineStore.admin.security.tenant;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;


@Configuration
public class FilterConfig {

    // الفلتر اللي بيفعل tenantId
    @Bean
    public FilterRegistrationBean<TenantIdFilter> tenantFilterRegistration(TenantIdFilter filter) {
        FilterRegistrationBean<TenantIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
//        registration.setOrder(1); // لازم يكون الأول
        registration.setName("TenantIdFilter");
        return registration;
    }

    // الفلتر اللي بيفتح session لحد نهاية الـ request
    @Bean
    public FilterRegistrationBean<OpenEntityManagerInViewFilter> openEntityManagerInViewFilter() {
        FilterRegistrationBean<OpenEntityManagerInViewFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new OpenEntityManagerInViewFilter());
//        registration.setOrder(2); // بعد فلتر tenant
        registration.setName("openEntityManagerInViewFilter");
        return registration;
    }
}
