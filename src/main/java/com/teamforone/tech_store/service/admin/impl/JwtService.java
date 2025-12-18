package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.model.NhanVien;
import com.teamforone.tech_store.model.User;
import com.teamforone.tech_store.repository.admin.RBAC.NhanVienRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${app.auth.tokenSecret}")
    private String secretKey;

    @Value("${app.auth.tokenExpiration}")
    private long expiration;

    @Value("${app.auth.refreshTokenExpiration}")
    private long refreshExpiration;

    private final NhanVienRepository nhanVienRepository;
    public String generateToken(UserDetails userDetails, String userType) {
        Map<String, Object> claims = new HashMap<>();

        if (userDetails instanceof User user) {
            claims.put("id", user.getId());
            claims.put("type", "USER");
            claims.put("role", "USER");

        } else if (userDetails instanceof NhanVien nv) {
            claims.put("id", nv.getId());
            claims.put("type", "NHAN_VIEN");

            String role = nv.getRoles().stream()
                    .findFirst()
                    .map(r -> r.getRoleName().name())
                    .orElse("STAFF");

            claims.put("role", role);
        }

        return buildToken(claims, userDetails, expiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Thêm roles vào refresh token
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("roles", roles);
        return buildToken(claims, userDetails, refreshExpiration);
    }

    private String buildToken(Map<String, Object> claims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(secretKey))
                .compact();

    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private SecretKey getSignInKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("id", String.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public String extractUserType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }


    public boolean verifyToken(String token) {
        if (isTokenExpired(token)) return false;

        String username = extractUsername(token);
        if (!StringUtils.hasText(username)) return false;

        return nhanVienRepository.findByUsername(username).isPresent();
    }

}
