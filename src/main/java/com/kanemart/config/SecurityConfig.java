package com.kanemart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // Disable CSRF for our REST API during development
            .csrf(csrf -> csrf.disable())

            // Allow our custom pages and authentication APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/login",
                    "/otp",
                    "/css/**",
                    "/js/**",
                    "/api/auth/**"
                ).permitAll()

                // Allow everything else for now
                .anyRequest().permitAll()
            )

            // Disable default Spring Security login page
            .formLogin(form -> form.disable())

            // Disable browser basic-auth popup
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}