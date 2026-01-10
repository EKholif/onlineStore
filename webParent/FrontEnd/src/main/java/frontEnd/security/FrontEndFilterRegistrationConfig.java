package frontEnd.security;

import com.onlineStoreCom.security.tenant.TenantContextFilter;
import frontEnd.setting.SettingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FrontEndFilterRegistrationConfig {

    @Autowired
    private TenantContextFilter tenantContextFilter;

    @Autowired
    private SettingFilter settingFilter;

    @Bean
    public FilterRegistrationBean<TenantContextFilter> tenantContextFilterRegistration() {
        FilterRegistrationBean<TenantContextFilter> registration = new FilterRegistrationBean<>(tenantContextFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<SettingFilter> settingFilterRegistration() {
        FilterRegistrationBean<SettingFilter> registration = new FilterRegistrationBean<>(settingFilter);
        registration.setEnabled(false);
        return registration;
    }
}
