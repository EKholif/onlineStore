package com.onlineStore.admin.security;

import com.onlineStore.admin.security.tenant.CustomLoginSuccessHandler;
import com.onlineStoreCom.security.tenant.TenantContextFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * AG-SEC-CONFIG-001: Spring Security Configuration with RBAC Support
 * <p>
 * Enables:
 * - Method-level security via @PreAuthorize
 * - Custom PermissionEvaluator for tenant-aware permissions
 * - TenantContextFilter for multi-tenancy
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // AG-RBAC-001: Enable @PreAuthorize
public class WebSecurityConfig {

    @Autowired
    private CustomLoginSuccessHandler customLoginSuccessHandler;

    @Autowired
    private TenantContextFilter tenantContextFilter;

    @Autowired
    private TenantPermissionEvaluator tenantPermissionEvaluator;

    @Autowired
    private TenantAccessValidationFilter tenantAccessValidationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/images/**", "/js/**", "/webjars/**", "/css/**", "/tenants/**",
                                "/tracking/**", "/test/**") // Added /test/** for temporary test endpoint
                        .permitAll()
                        .requestMatchers("/users/**", "/get_shipping_cost/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/customer/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/categories/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/brands/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/products/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/pdf-convert/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/settings/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/shipping-rate/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/orders_shipper/update//**").hasAnyAuthority("Shipper")
                        .anyRequest().authenticated())
                .formLogin((form) -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .successHandler(customLoginSuccessHandler)
                        .permitAll())
                .rememberMe(rememberMe -> rememberMe
                        .key("BqRqADxmG8iRXXLvwIZ47NY4")
                        .tokenValiditySeconds(14 * 24 * 60 * 60)
                        .userDetailsService(userDetailsService())) // Explicitly inject UserDetailsService for stability
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID", "remember-me") // Ensure clean session kill
                        .permitAll())
                .sessionManagement(session -> session
                        .sessionFixation().changeSessionId() // AG-SECURITY: Safer than migrateSession
                        // for stability
                        .maximumSessions(-1) // AG-STABILITY: Disable concurrency control to prevent
                        // accidental logouts
                        .expiredUrl("/login?expired")) // Redirect if session dies
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                        .contentSecurityPolicy(
                                csp -> csp.policyDirectives("frame-ancestors 'self' http://localhost:710")));

        // [AG-TEN-ARCH-002] Add Unified TenantContextFilter from comm module
        // [AG-TEN-SEC-009] Reorder to properly isolate tenant data before
        // authentication
        // [AG-SEC-FILTER-002] Add TenantAccessValidationFilter BEFORE
        // TenantContextFilter
        http.addFilterBefore(tenantAccessValidationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(tenantContextFilter, TenantAccessValidationFilter.class);

        return http.build();
    }

    /**
     * AG-RBAC-002: Register Custom PermissionEvaluator
     * <p>
     * Enables @PreAuthorize("hasPermission(null, 'PERMISSION_NAME')")
     * integrating with Role-Permission system
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(tenantPermissionEvaluator);
        return expressionHandler;
    }
}
