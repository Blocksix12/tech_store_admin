package com.teamforone.tech_store.repository.admin.RBAC;

import com.teamforone.tech_store.model.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, String> {
    Optional<NhanVien> findByUsername(String username);
    Optional<NhanVien> findByEmail(String email);
    Optional<NhanVien> findByPhone(String phoneNumber);
}
