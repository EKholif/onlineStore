package com.example.frontend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {


//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new SUserDetailsService();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(
//            AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//        authenticationProvider.setUserDetailsService(userDetailsService());
//        authenticationProvider.setPasswordEncoder(passwordEncoder());
//        return authenticationProvider;
//    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().anyRequest().permitAll();

//                .authenticationProvider(authenticationProvider())
//                .authorizeHttpRequests((requests) -> requests
//
//                        .requestMatchers("/users/**").hasAnyAuthority("Admin", "Editor")
//                        .requestMatchers("/categories/**").hasAnyAuthority("Admin", "Editor")
//                        .requestMatchers("/brands/**").hasAnyAuthority("Admin", "Editor")
//                        .requestMatchers("/products/**").hasAnyAuthority("Admin", "Editor")
//                        .requestMatchers("/pdf-convert/**").hasAnyAuthority("Admin", "Editor")
//
//                        .anyRequest().authenticated()
//                )
//                .formLogin((form) -> form
//                        .loginPage("/login")
//                        .usernameParameter("email")
//                        .permitAll()
//
//
//                )
//
//                .rememberMe(rememberMe -> rememberMe.key("BqRqADxmG8iRXXLvwIZ47NY4")
//                        .tokenValiditySeconds(1 * 24 * 60 * 60))
//                .logout(LogoutConfigurer::permitAll);


        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**", "/css/**");
    }


}
