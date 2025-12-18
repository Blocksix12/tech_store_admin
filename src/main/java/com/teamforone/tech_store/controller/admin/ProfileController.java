package com.teamforone.tech_store.controller.admin;

import com.teamforone.tech_store.dto.request.ProfileUpdateDTO;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.NhanVien;
import com.teamforone.tech_store.service.admin.NhanVienService;
import com.teamforone.tech_store.service.admin.impl.FileStorageService;
import com.teamforone.tech_store.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/admin/profile")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF','MANAGER','ADMIN')")
public class ProfileController {
    private final SecurityUtils securityUtils;
    private final NhanVienService nhanVienService;
    private final FileStorageService fileStorageService;

    @GetMapping("/{id}")
    public String viewProfile(@PathVariable String id, Model model) {
        // 🔐 ID THẬT từ JWT
        String currentUserId = securityUtils.getCurrentUserId();

        if (currentUserId == null) {
            return "redirect:http://localhost:8082/auth/login";
        }

        // 🚫 Không cho xem profile người khác
        if (!currentUserId.equals(id)) {
            return "redirect:/access-denied";
        }

        NhanVien nv = nhanVienService.findNhanVienById(currentUserId);
        if (nv == null) {
            model.addAttribute("error", "Nhân viên không tồn tại.");
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("profile", nv);
        return "Profile"; // profile.html
    }

    @PostMapping("/update")
    public String updateProfile(
            @Valid  @ModelAttribute("profile") ProfileUpdateDTO dto,  // ← Bỏ @ModelAttribute
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        String currentUserId = securityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return "redirect:http://localhost:8082/auth/login";
        }

        // Validation lỗi
        if (bindingResult.hasErrors()) {
            // Log lỗi để debug
            bindingResult.getAllErrors().forEach(error ->
                    log.error("Validation error: {}", error.getDefaultMessage())
            );

            redirectAttributes.addFlashAttribute("error",
                    "Dữ liệu không hợp lệ: " +
                            bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/profile/" + currentUserId + "?tab=edit";
        }

        try {
            nhanVienService.updateProfile(currentUserId, dto);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        } catch (RuntimeException e) {
            log.error("Error updating profile: ", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/profile/" + currentUserId;
    }

    @PostMapping("/update-avatar")
    public String updateAvatar(
            @RequestParam("maNhanVien") String id,
            @RequestParam("avatar") MultipartFile file,
            RedirectAttributes redirectAttributes
    ) {
        try {
            String currentUserId = securityUtils.getCurrentUserId();
            if (currentUserId == null) {
                return "redirect:http://localhost:8082/auth/login";
            }

            if (!currentUserId.equals(id)) {
                redirectAttributes.addFlashAttribute("error",
                        "Bạn không có quyền cập nhật avatar này!");
                return "redirect:/admin/profile/" + currentUserId;
            }

            if (file == null || file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error",
                        "Vui lòng chọn ảnh hợp lệ!");
                return "redirect:/admin/profile/" + currentUserId;
            }

            if (!file.getContentType().startsWith("image/")) {
                redirectAttributes.addFlashAttribute("error",
                        "Chỉ chấp nhận file ảnh!");
                return "redirect:/admin/profile/" + currentUserId;
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                redirectAttributes.addFlashAttribute("error",
                        "Kích thước ảnh tối đa 5MB!");
                return "redirect:/admin/profile/" + currentUserId;
            }

            NhanVien nv = nhanVienService.findNhanVienById(id);
            String oldAvatar = nv.getAvatarUrl();

            String newAvatarUrl = fileStorageService.saveFile(file);
            nhanVienService.updateAvatar(id, newAvatarUrl);

            if (oldAvatar != null && !oldAvatar.contains("ui-avatars.com")) {
                fileStorageService.deleteFile(oldAvatar);
            }

            redirectAttributes.addFlashAttribute("success",
                    "Cập nhật ảnh đại diện thành công!");

        } catch (Exception e) {
            log.error("Upload avatar error", e);
            redirectAttributes.addFlashAttribute("error",
                    "Có lỗi xảy ra khi cập nhật avatar!");
        }

        return "redirect:/admin/profile/" + securityUtils.getCurrentUserId();
    }
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
            // 🔐 Kiểm tra quyền
            String currentUserId = securityUtils.getCurrentUserId();
            if (currentUserId == null) {
                return "redirect:http://localhost:8082/auth/login";
            }

            // Kiểm tra mật khẩu mới và xác nhận khớp
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp!");
                return "redirect:/admin/profile/" + currentUserId + "?tab=security";
            }

            // Kiểm tra độ dài mật khẩu
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
                return "redirect:/admin/profile/" + currentUserId + "?tab=security";
            }

            // Đổi mật khẩu
            nhanVienService.changePassword(currentUserId, currentPassword, newPassword);

            redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
            return "redirect:/admin/profile/" + currentUserId + "?tab=security";

        } catch (RuntimeException e) {
            log.error("Error changing password: ", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            String currentUserId = securityUtils.getCurrentUserId();
            return "redirect:/admin/profile/" + currentUserId + "?tab=security";
        } catch (Exception e) {
            log.error("Unexpected error changing password: ", e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            String currentUserId = securityUtils.getCurrentUserId();
            return "redirect:/admin/profile/" + currentUserId + "?tab=security";
        }
    }
}
