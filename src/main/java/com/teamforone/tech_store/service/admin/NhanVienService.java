package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.NhanVienUpdateRequest;
import com.teamforone.tech_store.dto.request.ProfileUpdateDTO;
import com.teamforone.tech_store.dto.request.RegisterRequest;
import com.teamforone.tech_store.dto.response.NhanVienResponse;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.NhanVien;

import java.io.IOException;
import java.util.Optional;

public interface NhanVienService {
    Response createNhanVien(RegisterRequest request) throws IOException;
    Response updateNhanVien(String id, NhanVienUpdateRequest request) throws IOException;
    NhanVien deleteNhanVien(String id);
    NhanVien findNhanVienById(String id);
    Optional<NhanVien> findByUsername(String username);
    void updateProfile(String id, ProfileUpdateDTO dto);
    void updateAvatar(String id, String avatarUrl);
    void changePassword(String id, String currentPassword, String newPassword);

}
