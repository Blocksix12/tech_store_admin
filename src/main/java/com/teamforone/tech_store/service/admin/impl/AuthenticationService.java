package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.LoginRequest;
import com.teamforone.tech_store.dto.response.LoginResponse;
import com.teamforone.tech_store.dto.response.VerifyTokenResponse;
import com.teamforone.tech_store.model.NhanVien;
import com.teamforone.tech_store.repository.admin.RBAC.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final int TOKEN_INDEX = 7;

    private final NhanVienRepository nhanVienRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public LoginResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        Optional<NhanVien> userEntity = nhanVienRepository.findByUsername(username);
        if (userEntity.isEmpty()){
            return LoginResponse.builder()
                    .status(404)
                    .message("User not found")
                    .build();
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);

        authenticationManager.authenticate(authToken);

        NhanVien user = userEntity.get();
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);

        nhanVienRepository.save(user);

        return LoginResponse.builder()
                .status(200)
                .message("Login successful")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    public LoginResponse refreshToken(String authHeader) {
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return LoginResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid token")
                    .build();
        }
        String refreshToken = authHeader.substring(TOKEN_INDEX);
        if( !jwtService.verifyToken(refreshToken)) {
            return LoginResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid refresh token")
                    .build();
        }

        log.info("Refreshing token for: {}", refreshToken);
        String username = jwtService.extractUsername(refreshToken);
        Optional<NhanVien> userFoundByUsername = nhanVienRepository.findByUsername(username);
        if (userFoundByUsername.isEmpty()) {
            return LoginResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Token revoked")
                    .build();
        }

        NhanVien nhanVien = userFoundByUsername.get();
        String accessToken = jwtService.generateToken(nhanVien);
        String newRefreshToken = jwtService.generateRefreshToken(nhanVien);

        nhanVien.setAccessToken(accessToken);
        nhanVien.setRefreshToken(newRefreshToken);
        nhanVienRepository.save(nhanVien);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .userId(nhanVien.getId())
                .message("Refresh token successfully")
                .status(HttpStatus.OK.value())
                .build();
    }

    public VerifyTokenResponse verifyToken(String authHeader) {
        log.info("verifyToken|authHeader: {}", authHeader);

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.error("verifyToken|Authorization header is missing or invalid");
            return VerifyTokenResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid token")
                    .build();
        }

        String token = authHeader.substring(TOKEN_INDEX);
        if (!jwtService.verifyToken(token)) {
            log.error("verifyToken|Invalid token");
            return VerifyTokenResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid token")
                    .build();
        }

        String username = jwtService.extractUsername(token);
        Optional<NhanVien> userFoundByUsername = nhanVienRepository.findByUsername(username);
        if (userFoundByUsername.isEmpty()) {
            log.error("verifyToken|User not found for username: {}", username);
            return VerifyTokenResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Token revoked")
                    .build();
        }

        Set<String> roles = userFoundByUsername.get().getRoles()
                .stream()
                .map(r -> r.getRoleName().name())
                .collect(Collectors.toSet());

        String userInfoStr = username + ":" + roles;
        String xUserToken = Base64.getEncoder().encodeToString(userInfoStr.getBytes());

        log.info("verifyToken|X-User-Token: {}", xUserToken);

        return VerifyTokenResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Success")
                .xUserToken(xUserToken)
                .build();
    }
}
