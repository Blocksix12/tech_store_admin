package com.teamforone.tech_store.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
            "/auth/admin/register",
            "/admin/2fa/phone/**",
            "/admin/2fa/email/**",
            "/auth/admin/update/{id}",
            "/auth/login",
            "/api/v1/auth/refresh-token",
            "/api/v1/auth/verify",
            "/admin/**",
            "/css/**",
            "/js/**",
            "/javascript/**",
            "/images/**",
            "/static/**",
            "/favicon.ico",
            "/webjars/**"
    };



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity htpp) throws Exception {
        htpp.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST_URL).permitAll()
                        .anyRequest().authenticated()
                ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return htpp.build();
    }
}
