package com.teamforone.tech_store.repository.admin;

import com.teamforone.tech_store.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByNhanVienIdAndRevokedFalse(String nhanVienId);

    List<RefreshToken> findByUserIdAndRevokedFalse(String userId);
}
