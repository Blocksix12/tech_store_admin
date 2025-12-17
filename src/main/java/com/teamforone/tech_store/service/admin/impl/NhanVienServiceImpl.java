package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.NhanVienUpdateRequest;
import com.teamforone.tech_store.dto.request.RegisterRequest;
import com.teamforone.tech_store.dto.response.NhanVienResponse;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.NhanVien;
import com.teamforone.tech_store.repository.admin.RBAC.NhanVienRepository;
import com.teamforone.tech_store.repository.admin.RBAC.RoleRepository;
import com.teamforone.tech_store.service.admin.NhanVienService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class NhanVienServiceImpl implements NhanVienService {
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public Response createNhanVien(RegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String fullName = request.getFullName();
        String email = request.getEmail();
        String phoneNumber = request.getPhoneNumber();

        Optional<NhanVien> nhanVienByUsername = nhanVienRepository.findByUsername(username);
        Optional<NhanVien> nhanVienByEmail = nhanVienRepository.findByEmail(email);

        if(nhanVienByUsername.isPresent() || nhanVienByEmail.isPresent()) {
            return Response.builder()
                    .status(400)
                    .message("Username or Email already exists")
                    .build();
        }

        NhanVien newNhanVien = NhanVien.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .fullname(fullName)
                .email(email)
                .phone(phoneNumber)
                .build();
        nhanVienRepository.save(newNhanVien);
        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("NhanVien registered successfully")
                .build();
    }

    @Override
    public Response updateNhanVien(String id, NhanVienUpdateRequest request) {
        NhanVien existingNhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NhanVien not found"));

        existingNhanVien.setUsername(request.getUsername());
        existingNhanVien.setFullname(request.getFullName());
        existingNhanVien.setPassword(passwordEncoder.encode(request.getPassword()));
        existingNhanVien.setEmail(request.getEmail());
        existingNhanVien.setPhone(request.getPhoneNumber());

        var roles = roleRepository.findAllById(request.getRoles());
        existingNhanVien.setRoles(new HashSet<>(roles));

        nhanVienRepository.save(existingNhanVien);

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("NhanVien updated successfully")
                .build();
    }
}
