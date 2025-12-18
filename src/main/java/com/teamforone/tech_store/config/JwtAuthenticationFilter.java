package com.teamforone.tech_store.config;

import com.teamforone.tech_store.service.admin.impl.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // ✅ Các path cần kiểm tra JWT (sync với SecurityConfiguration)
    private static final String[] SECURED_PATHS = {
            "/admin/api/",
            "/admin/permissions/",
            "/admin/roles/"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // ✅ CHỈ kiểm tra JWT cho secured API paths
        boolean isSecuredPath = Arrays.stream(SECURED_PATHS)
                .anyMatch(requestPath::startsWith);

        if (!isSecuredPath) {
            // Bỏ qua JWT cho các trang HTML views và public endpoints
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Kiểm tra Authorization header
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.verifyToken(token)) {
                    // Lấy roles từ token
                    String rolesString = jwtService.extractRoles(token);
                    List<GrantedAuthority> authorities;

                    if (StringUtils.hasText(rolesString)) {
                        authorities = Arrays.stream(rolesString.split(","))
                                .map(String::trim)
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                    } else {
                        authorities = Collections.emptyList();
                    }

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log lỗi nhưng không block request
            System.err.println("JWT validation error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}