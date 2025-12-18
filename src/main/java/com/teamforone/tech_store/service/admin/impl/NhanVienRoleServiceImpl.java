package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.model.NhanVien;
import com.teamforone.tech_store.model.Roles;
import com.teamforone.tech_store.repository.admin.RBAC.NhanVienRepository;
import com.teamforone.tech_store.repository.admin.RBAC.RoleRepository;
import com.teamforone.tech_store.service.admin.NhanVienRoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NhanVienRoleServiceImpl implements NhanVienRoleService {
    private final NhanVienRepository nhanVienRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void assignRoles(String nhanVienId, List<String> roleIds) {
        log.info("Gán roles cho nhân viên ID: {}, roleIds: {}", nhanVienId, roleIds);

        // Tìm nhân viên
        NhanVien nhanVien = nhanVienRepository.findById(nhanVienId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + nhanVienId));

        // Tìm các roles
        List<Roles> rolesToAdd = roleRepository.findAllById(roleIds);
        if (rolesToAdd.size() != roleIds.size()) {
            throw new RuntimeException("Một số role không tồn tại trong hệ thống");
        }

        // Lấy roles hiện tại (nếu chưa có thì tạo mới)
        Set<Roles> currentRoles = nhanVien.getRoles();
        if (currentRoles == null) {
            currentRoles = new HashSet<>();
            nhanVien.setRoles(currentRoles);
        }

        // Kiểm tra trùng lặp
        Set<String> existingRoleIds = currentRoles.stream()
                .map(Roles::getRoleID)
                .collect(Collectors.toSet());

        int addedCount = 0;
        for (Roles role : rolesToAdd) {
            if (!existingRoleIds.contains(role.getRoleID())) {
                currentRoles.add(role);
                addedCount++;
            }
        }

        if (addedCount == 0) {
            throw new RuntimeException("Tất cả các vai trò đã được gán trước đó");
        }

        // Lưu
        nhanVienRepository.save(nhanVien);
        log.info("Đã gán thành công {} vai trò cho nhân viên {}", addedCount, nhanVien.getUsername());
    }

    @Override
    @Transactional
    public void removeRole(String nhanVienId, String roleId) {
        log.info("Xóa role {} khỏi nhân viên ID: {}", roleId, nhanVienId);

        // Tìm nhân viên
        NhanVien nhanVien = nhanVienRepository.findById(nhanVienId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + nhanVienId));

        // Kiểm tra roles
        Set<Roles> currentRoles = nhanVien.getRoles();
        if (currentRoles == null || currentRoles.isEmpty()) {
            throw new RuntimeException("Nhân viên chưa có vai trò nào");
        }

        // Tìm và xóa role
        boolean removed = currentRoles.removeIf(role -> role.getRoleID().equals(roleId));

        if (!removed) {
            throw new RuntimeException("Nhân viên không có vai trò này");
        }

        // Lưu
        nhanVienRepository.save(nhanVien);
        log.info("Đã xóa vai trò thành công khỏi nhân viên {}", nhanVien.getUsername());
    }

    @Override
    @Transactional
    public void clearAllRoles(String nhanVienId) {
        log.info("Xóa tất cả roles của nhân viên ID: {}", nhanVienId);

        // Tìm nhân viên
        NhanVien nhanVien = nhanVienRepository.findById(nhanVienId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + nhanVienId));

        // Xóa tất cả roles
        Set<Roles> currentRoles = nhanVien.getRoles();
        if (currentRoles == null || currentRoles.isEmpty()) {
            throw new RuntimeException("Nhân viên chưa có vai trò nào");
        }

        int count = currentRoles.size();
        currentRoles.clear();

        // Lưu
        nhanVienRepository.save(nhanVien);
        log.info("Đã xóa {} vai trò khỏi nhân viên {}", count, nhanVien.getUsername());
    }

    @Override
    @Transactional
    public void replaceRoles(String nhanVienId, List<String> roleIds) {
        log.info("Thay thế toàn bộ roles cho nhân viên ID: {}, roleIds mới: {}", nhanVienId, roleIds);

        // Tìm nhân viên
        NhanVien nhanVien = nhanVienRepository.findById(nhanVienId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + nhanVienId));

        // Tìm các roles mới
        List<Roles> newRoles = roleRepository.findAllById(roleIds);
        if (newRoles.size() != roleIds.size()) {
            throw new RuntimeException("Một số role không tồn tại trong hệ thống");
        }

        // Xóa roles cũ và thêm roles mới
        Set<Roles> roleSet = new HashSet<>(newRoles);
        nhanVien.setRoles(roleSet);

        // Lưu
        nhanVienRepository.save(nhanVien);
        log.info("Đã thay thế thành công {} vai trò cho nhân viên {}", roleSet.size(), nhanVien.getUsername());
    }

    @Override
    public boolean hasRole(String nhanVienId, String roleId) {
        NhanVien nhanVien = nhanVienRepository.findById(nhanVienId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + nhanVienId));

        Set<Roles> roles = nhanVien.getRoles();
        if (roles == null || roles.isEmpty()) {
            return false;
        }

        return roles.stream()
                .anyMatch(role -> role.getRoleID().equals(roleId));
    }
}
