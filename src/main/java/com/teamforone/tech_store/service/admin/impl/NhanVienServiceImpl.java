package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.NhanVienUpdateRequest;
import com.teamforone.tech_store.dto.request.ProfileUpdateDTO;
import com.teamforone.tech_store.dto.request.RegisterRequest;
import com.teamforone.tech_store.dto.response.NhanVienResponse;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.enums.Gender;
import com.teamforone.tech_store.model.NhanVien;
import com.teamforone.tech_store.model.Product;
import com.teamforone.tech_store.repository.admin.RBAC.NhanVienRepository;
import com.teamforone.tech_store.repository.admin.RBAC.RoleRepository;
import com.teamforone.tech_store.service.admin.NhanVienService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class NhanVienServiceImpl implements NhanVienService {
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Response createNhanVien(RegisterRequest request) throws IOException {
        String username = request.getUsername();
        String password = request.getPassword();
        String fullName = request.getFullName();
        String email = request.getEmail();
        String phoneNumber = request.getPhoneNumber();
        String avatarUrl = null;
        if (request.getAvatarFile() != null && ! request.getAvatarFile().isEmpty()) {
            avatarUrl = fileStorageService.saveFile(request.getAvatarFile());
        }

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
                .fullName(fullName)
                .email(email)
                .phone(phoneNumber)
                .avatarUrl(avatarUrl)
                .status(NhanVien.Status.ACTIVE)
                .build();
        nhanVienRepository.save(newNhanVien);
        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("NhanVien registered successfully")
                .build();
    }

    @Override
    @Transactional
    public Response updateNhanVien(String id, NhanVienUpdateRequest request) throws IOException {

        NhanVien nv = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NhanVien not found"));

        nv.setUsername(request.getUsername());
        nv.setFullName(request.getFullName());

        // Password
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            nv.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Email
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String email = request.getEmail().trim().toLowerCase();
            nhanVienRepository.findByEmail(email)
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new RuntimeException("Email đã tồn tại");
                        }
                    });
            nv.setEmail(email);
        }

        // Phone
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            String phone = request.getPhoneNumber().trim();
            nhanVienRepository.findByPhone(phone)
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new RuntimeException("Số điện thoại đã tồn tại");
                        }
                    });
            nv.setPhone(phone);
        }

        // Avatar
        MultipartFile newImage = request.getAvatarFile();
        if (newImage != null && !newImage.isEmpty()) {
            String newUrl = fileStorageService.saveFile(newImage);
            String oldUrl = nv.getAvatarUrl();
            nv.setAvatarUrl(newUrl);

            if (oldUrl != null && !oldUrl.isBlank()) {
                try {
                    fileStorageService.deleteFile(oldUrl);
                } catch (Exception ignored) {}
            }
        }

        // Gender
        if (request.getGender() != null && !request.getGender().isBlank()) {
            try {
                nv.setGender(Gender.valueOf(request.getGender()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Giới tính không hợp lệ");
            }
        }

        nv.setDateOfBirth(request.getDateOfBirth());
        nv.setAddress(request.getAddress());
        nv.setBio(request.getBio());
        nv.setWebsite(request.getWebsite());

        // Status
        nv.setStatus(request.isActive()
                ? NhanVien.Status.ACTIVE
                : NhanVien.Status.LOCKED);

        // Roles
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            var roles = roleRepository.findAllById(request.getRoles());
            nv.setRoles(new HashSet<>(roles));
        }

        nhanVienRepository.save(nv);

        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật nhân viên thành công")
                .build();
    }

    @Transactional
    @Override
    public void updateProfile(String id, ProfileUpdateDTO dto) {
        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));

        // 1. Full name
        nhanVien.setFullName(dto.getFullName().trim());

        // 2. Email
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            String email = dto.getEmail().trim().toLowerCase();
            nhanVienRepository.findByEmail(email)
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new RuntimeException("Email đã được sử dụng");
                        }
                    });
            nhanVien.setEmail(email);
        } else {
            nhanVien.setEmail(null);
        }

        // 3. Phone
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            String phone = dto.getPhone().trim();
            nhanVienRepository.findByPhone(phone)
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new RuntimeException("Số điện thoại đã được sử dụng");
                        }
                    });
            nhanVien.setPhone(phone);
        } else {
            nhanVien.setPhone(null);
        }

        // 4. Date of birth
        nhanVien.setDateOfBirth(dto.getDateOfBirth());

        // 5. Gender
        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            try {
                nhanVien.setGender(Gender.valueOf(dto.getGender()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Giới tính không hợp lệ");
            }
        } else {
            nhanVien.setGender(null);
        }

        // 6. Address
        nhanVien.setAddress(isBlank(dto.getAddress()) ? null : dto.getAddress().trim());

        // 7. Bio
        nhanVien.setBio(isBlank(dto.getBio()) ? null : dto.getBio().trim());

        // 8. Website
        nhanVien.setWebsite(isBlank(dto.getWebsite()) ? null : dto.getWebsite().trim());

        nhanVienRepository.save(nhanVien);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Transactional
    @Override
    public void updateAvatar(String id, String avatarUrl) {
        NhanVien nhanVien = findNhanVienById(id);
        nhanVien.setAvatarUrl(avatarUrl);
        nhanVienRepository.save(nhanVien);
    }

    @Transactional
    @Override
    public void changePassword(String id, String currentPassword, String newPassword) {
        NhanVien nhanVien = findNhanVienById(id);

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, nhanVien.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }

        // Kiểm tra độ dài mật khẩu mới
        if (newPassword.length() < 6) {
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự");
        }

        // Cập nhật mật khẩu mới
        nhanVien.setPassword(passwordEncoder.encode(newPassword));
        nhanVienRepository.save(nhanVien);
    }

    @Override
    @Transactional
    public NhanVien deleteNhanVien(String id) {
        NhanVien nhanVien = nhanVienRepository.findById(id).orElse(null);
        if (nhanVien == null) {
            return null;
        }

        try {

            if (nhanVien.getAvatarUrl() != null && !nhanVien.getAvatarUrl().isEmpty()) {
                try {
                    fileStorageService.deleteFile(nhanVien.getAvatarUrl());
                } catch (Exception e) {
                    System.err.println("Không thể xóa ảnh Nhân Viên: " + e.getMessage());
                }
            }

            nhanVienRepository.delete(nhanVien);

            return nhanVien;

        } catch (Exception e) {
            System.err.println("Lỗi khi xóa Nhân Viên: " + e.getMessage());
            throw new RuntimeException("Không thể xóa Nhân Viên: " + e.getMessage());
        }
    }

    @Override
    public NhanVien findNhanVienById(String id) {
        return nhanVienRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<NhanVien> findByUsername(String username) {
        return nhanVienRepository.findByUsername(username);
    }
}
