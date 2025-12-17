package com.teamforone.tech_store.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    // ✅ URLs không cần JWT token (public access)
    private static final String[] WHITE_LIST_URL = {
            // Auth endpoints
            "/auth/**",

            // Static resources
            "/css/**",
            "/js/**",
            "/javascript/**",
            "/images/**",
            "/static/**",
            "/favicon.ico",
            "/webjars/**",

            // Admin pages (HTML views) - không cần JWT
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
            "/admin/2fa/**",
            "/admin/CTProduct",
            "/admin/addCTProduct",
            "/admin/product-variants",
            "/admin/product-variants/**",
            "/admin/attributes",
            "/admin/attributes/**"
    };

    // ✅ URLs cần JWT token (API endpoints)
    private static final String[] SECURED_API_URL = {
            "/admin/api/**",
            "/admin/permissions/**",
            "/admin/roles/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // ✅ Cho phép truy cập không cần JWT
                        .requestMatchers(WHITE_LIST_URL).permitAll()

                        // ✅ Yêu cầu JWT cho API endpoints
                        .requestMatchers(SECURED_API_URL).authenticated()

                        // ✅ Tất cả request khác cũng cho phép (vì là web app)
                        .anyRequest().permitAll()
                )
                // ✅ Session stateless cho API, nhưng vẫn cho phép session cho web pages
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                // ✅ Chỉ áp dụng JWT filter cho secured APIs
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}