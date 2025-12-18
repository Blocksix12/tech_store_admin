package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.LoginRequest;
import com.teamforone.tech_store.dto.response.LoginResponse;
import com.teamforone.tech_store.dto.response.VerifyTokenResponse;
import com.teamforone.tech_store.model.NhanVien;
import com.teamforone.tech_store.model.RefreshToken;
import com.teamforone.tech_store.model.User;
import com.teamforone.tech_store.repository.admin.RBAC.NhanVienRepository;
import com.teamforone.tech_store.repository.admin.RefreshTokenRepository;
import com.teamforone.tech_store.repository.admin.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
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
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;


    public LoginResponse login(LoginRequest loginRequest) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // ===== TRY NHANVIEN =====
        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByUsername(username);
        if (nhanVienOpt.isPresent()) {

            NhanVien nhanVien = nhanVienOpt.get();

            // 1️⃣ Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // 2️⃣ Lấy role GIỐNG NHƯ generateToken và RefreshTokenService
            String role = nhanVien.getRoles().stream()
                    .findFirst()
                    .map(r -> r.getRoleName().name())
                    .orElse("STAFF");

            Set<String> roles = Set.of("ROLE_" + role);

            // 3️⃣ Generate JWT Access Token
            String accessToken = jwtService.generateToken(nhanVien, "NHAN_VIEN");

            // 4️⃣ Create RefreshToken using RefreshTokenService
            RefreshToken refreshToken = refreshTokenService.createForNhanVien(nhanVien);

            // 5️⃣ Save access token vào NhanVien (optional - nếu cần)
            nhanVien.setAccessToken(accessToken);
            nhanVienRepository.save(nhanVien);

            // 6️⃣ Response
            return LoginResponse.builder()
                    .status(200)
                    .message("Login successful")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken()) // ✅ Lấy token string
                    .userId(nhanVien.getId())
                    .username(username)
                    .userType("NHAN_VIEN")
                    .roles(roles)
                    .build();
        }

        // ===== TRY USER =====
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {

            User user = userOpt.get();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            Set<String> roles = Set.of("ROLE_USER");

            // Generate JWT Access Token
            String accessToken = jwtService.generateToken(user, "USER");

            // Create RefreshToken using RefreshTokenService
            RefreshToken refreshToken = refreshTokenService.createForUser(user);

            // Save access token (optional)
            user.setAccessToken(accessToken);
            userRepository.save(user);

            return LoginResponse.builder()
                    .status(200)
                    .message("Login successful")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken()) // ✅ Lấy token string
                    .userId(user.getId())
                    .username(username)
                    .userType("USER")
                    .roles(roles)
                    .build();
        }

        return LoginResponse.builder()
                .status(404)
                .message("User not found")
                .build();
    }

    public LoginResponse refreshToken(String authHeader) {
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return LoginResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid token")
                    .build();
        }

        String refreshTokenString = authHeader.substring(TOKEN_INDEX);

        // 1️⃣ Tìm RefreshToken trong database
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(refreshTokenString);

        if (refreshTokenOpt.isEmpty()) {
            log.warn("Refresh token not found in database");
            return buildTokenRevokedResponse();
        }

        RefreshToken refreshToken = refreshTokenOpt.get();

        // 2️⃣ Kiểm tra token có bị revoke không
        if (refreshToken.isRevoked()) {
            log.warn("Refresh token has been revoked");
            return buildTokenRevokedResponse();
        }

        // 3️⃣ Kiểm tra token có hết hạn không
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            log.warn("Refresh token has expired");
            return LoginResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Refresh token expired")
                    .build();
        }

        // 4️⃣ Generate new tokens dựa vào tokenType
        if (refreshToken.getNhanVienId() != null) {
            // NHAN_VIEN
            Optional<NhanVien> nhanVienOpt = nhanVienRepository.findById(refreshToken.getNhanVienId());
            if (nhanVienOpt.isEmpty()) {
                return buildTokenRevokedResponse();
            }

            NhanVien nhanVien = nhanVienOpt.get();

            // Generate new access token
            String newAccessToken = jwtService.generateToken(nhanVien, "NHAN_VIEN");

            // Create new refresh token (rotate refresh token)
            refreshToken.setRevoked(true); // Revoke old token
            refreshTokenRepository.save(refreshToken);

            RefreshToken newRefreshToken = refreshTokenService.createForNhanVien(nhanVien);

            // Save new access token
            nhanVien.setAccessToken(newAccessToken);
            nhanVienRepository.save(nhanVien);

            return LoginResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Token refreshed successfully")
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken.getToken())
                    .userId(nhanVien.getId())
                    .username(nhanVien.getUsername())
                    .userType("NHAN_VIEN")
                    .build();
        }
        else if (refreshToken.getUserId() != null) {
            // USER
            Optional<User> userOpt = userRepository.findById(refreshToken.getUserId());
            if (userOpt.isEmpty()) {
                return buildTokenRevokedResponse();
            }

            User user = userOpt.get();

            // Generate new access token
            String newAccessToken = jwtService.generateToken(user, "USER");

            // Create new refresh token (rotate refresh token)
            refreshToken.setRevoked(true); // Revoke old token
            refreshTokenRepository.save(refreshToken);

            RefreshToken newRefreshToken = refreshTokenService.createForUser(user);

            // Save new access token
            user.setAccessToken(newAccessToken);
            userRepository.save(user);

            return LoginResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Token refreshed successfully")
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken.getToken())
                    .userId(user.getId())
                    .username(user.getUsername())
                    .userType("USER")
                    .build();
        }

        return buildTokenRevokedResponse();
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
    private VerifyTokenResponse buildVerifySuccessResponse(String username, Set<String> roles, String userType) {
        String userInfoStr = username + ":" + roles + ":" + userType;
        String xUserToken = Base64.getEncoder().encodeToString(userInfoStr.getBytes());

        log.info("verifyToken|X-User-Token: {}", xUserToken);

        return VerifyTokenResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Success")
                .xUserToken(xUserToken)
                .build();
    }

    private LoginResponse buildTokenRevokedResponse() {
        return LoginResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("Token revoked")
                .build();
    }

    private VerifyTokenResponse buildInvalidTokenResponse() {
        return VerifyTokenResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("Token revoked")
                .build();
    }

}
