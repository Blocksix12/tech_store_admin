package com.teamforone.tech_store.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    @Autowired
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] WHITE_LIST_URL = {
            "/auth/**",
            "/admin/2fa/**",
            "/css/**",
            "/js/**",
            "/javascript/**",
            "/images/**",
            "/static/**",
            "/favicon.ico",
            "/webjars/**",
            "/admin/dashboard",
            "/admin/products",
            "/admin/products/**",
            "/admin/categories",
            "/admin/categories/**",
            "/admin/brands",
            "/admin/brands/**",
            "/admin/users",
            "/admin/users/**",
            "/admin/reports",
            "/admin/reports/**",
            "/admin/orders",
            "/admin/orders/**",
            "/admin/inventory",
            "/admin/inventory/**",
            "/admin/settings",
            "/admin/settings/**",
            "/admin/profile",
            "/admin/CTProduct",
            "/admin/addCTProduct",
            "/admin/product-variants",
            "/admin/product-variants/**",
            "/admin/attributes",
            "/admin/attributes/**"
    };

    private static final String[] SECURED_API_URL = {
            "/admin/api/**",
            "/admin/permissions/**",
            "/admin/roles/**"
    };

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/api/**", "/admin/permissions/**", "/admin/roles/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SECURED_API_URL).authenticated()
                        .anyRequest().permitAll()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST_URL).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}