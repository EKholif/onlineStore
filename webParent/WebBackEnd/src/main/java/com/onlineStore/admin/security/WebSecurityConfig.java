package com.onlineStore.admin.security;

import com.onlineStore.admin.security.tenant.CustomLoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class for the Online Store Admin application.
 * <p>
 * This class configures Spring Security with form-based authentication, role-based authorization,
 * and various security settings. It defines security rules for different URL patterns and
 * configures the authentication provider.
 * 
 * @Configuration - Indicates that this class contains Spring configuration.
 * @EnableWebSecurity - Enables Spring Security's web security support.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * Configures the password encoder bean.
     * 
     * @return BCryptPasswordEncoder instance for password hashing
     */
    @Autowired
    private CustomLoginSuccessHandler customLoginSuccessHandler;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the custom UserDetailsService.
     * 
     * @return Custom UserDetailsService implementation
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService();
    }

    /**
     * Configures the AuthenticationManager.
     * 
     * @param authConfig AuthenticationConfiguration instance
     * @return Configured AuthenticationManager
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configures the authentication provider with custom UserDetailsService and password encoder.
     * 
     * @return Configured DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Configures the security filter chain with authorization rules and authentication settings.
     * 
     * @param http HttpSecurity to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/users/**","/get_shipping_cost/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/customer/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/categories/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/brands/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/products/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/pdf-convert/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/settings/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/shipping-rate/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/orders_shipper/update//**").hasAnyAuthority("Shipper")

                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .successHandler(customLoginSuccessHandler)
                        .permitAll()
                )

                .rememberMe(rememberMe -> rememberMe.key("BqRqADxmG8iRXXLvwIZ47NY4")
                        .tokenValiditySeconds(14 * 24 * 60 * 60))
                .logout(logout -> logout.permitAll())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives("frame-ancestors 'self' http://localhost:710")
                        )
                );

        return http.build();
    }

    /**
     * Configures web security to ignore static resources.
     * 
     * @return WebSecurityCustomizer that ignores static resources
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**", "/css/**");
    }
}
