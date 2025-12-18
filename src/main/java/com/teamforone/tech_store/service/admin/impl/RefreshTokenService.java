package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.enums.TokenType;
import com.teamforone.tech_store.model.NhanVien;
import com.teamforone.tech_store.model.RefreshToken;
import com.teamforone.tech_store.model.User;
import com.teamforone.tech_store.repository.admin.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepository repo;

    public RefreshToken createForNhanVien(NhanVien nv) {
        RefreshToken rt = new RefreshToken();
        rt.setToken(UUID.randomUUID().toString());
        rt.setNhanVienId(nv.getId());

        String role = nv.getRoles().stream()
                .findFirst()
                .map(r -> r.getRoleName().name())
                .orElse("STAFF");

        rt.setTokenType(TokenType.valueOf(role));
        rt.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
        return repo.save(rt);
    }

    public RefreshToken createForUser(User user) {
        RefreshToken rt = new RefreshToken();
        rt.setToken(UUID.randomUUID().toString());
        rt.setUserId(user.getId());
        rt.setTokenType(TokenType.USER);
        rt.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
        return repo.save(rt);
    }
}
