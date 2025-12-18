package com.teamforone.tech_store.controller.admin;

import com.teamforone.tech_store.model.NhanVien;
import com.teamforone.tech_store.repository.admin.RBAC.NhanVienRepository;
import com.teamforone.tech_store.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.UUID;


@ControllerAdvice
@RequiredArgsConstructor
public class GlobalUserModelAdvice {
    private final SecurityUtils securityUtils;
    private final NhanVienRepository nhanVienRepository;

    @ModelAttribute
    public void addUserInfo(Model model) {

        String userId = securityUtils.getCurrentUserId();
        if (userId == null) return;

        NhanVien nv = nhanVienRepository.findById(userId).orElse(null);
        if (nv == null) return;

        model.addAttribute("userId", nv.getId());          // String
        model.addAttribute("username", nv.getUsername());
        model.addAttribute("fullName", nv.getFullName());
        model.addAttribute("email", nv.getEmail());
        model.addAttribute("avatar", nv.getAvatarUrl());

        // nếu có nhiều role thì chọn role chính
        model.addAttribute("role", resolvePrimaryRole(nv));
    }
    private String resolvePrimaryRole(NhanVien nv) {
        return nv.getRoles().stream()
                .map(r -> r.getRoleName().name())
                .sorted((a, b) -> {
                    if (a.equals("ADMIN")) return -1;
                    if (b.equals("ADMIN")) return 1;
                    if (a.equals("MANAGER")) return -1;
                    if (b.equals("MANAGER")) return 1;
                    return 0;
                })
                .findFirst()
                .orElse("STAFF");
    }
}
