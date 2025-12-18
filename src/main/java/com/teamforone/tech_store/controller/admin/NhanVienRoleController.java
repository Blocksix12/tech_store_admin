package com.teamforone.tech_store.controller.admin;

import com.teamforone.tech_store.model.NhanVien;
import com.teamforone.tech_store.model.Roles;
import com.teamforone.tech_store.service.admin.NhanVienRoleService;
import com.teamforone.tech_store.service.admin.NhanVienService;
import com.teamforone.tech_store.service.admin.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/nhanvien")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class NhanVienRoleController {
    private final NhanVienService nhanVienService;
    private final RoleService roleService;
    private final NhanVienRoleService nhanVienRoleService;

    /**
     * Hiển thị trang phân quyền cho nhân viên
     */
    @GetMapping("/{id}/roles")
    public String showRoleAssignmentPage(@PathVariable String id,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        try {
            // Lấy thông tin nhân viên
            NhanVien nhanVien = nhanVienService.findNhanVienById(id);
            if (nhanVien == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhân viên");
                return "redirect:/admin/nhanvien";
            }

            // Lấy tất cả roles có sẵn
            List<Roles> allRoles = roleService.getAllRoles();

            // Lọc ra các role chưa được gán
            Set<String> currentRoleIds = nhanVien.getRoles() != null
                    ? nhanVien.getRoles().stream()
                    .map(Roles::getRoleID)
                    .collect(Collectors.toSet())
                    :Set.of();

            List<Roles> availableRoles = allRoles.stream()
                    .filter(role -> !currentRoleIds.contains(role.getRoleID()))
                    .collect(Collectors.toList());

            model.addAttribute("nhanVien", nhanVien);
            model.addAttribute("availableRoles", availableRoles);
            model.addAttribute("pageTitle", "Phân quyền nhân viên");

            return "NhanVienRoleManagement";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/nhanvien";
        }
    }

    /**
     * Gán roles cho nhân viên
     */
    @PostMapping("/{id}/roles/assign")
    public String assignRoles(@PathVariable String id,
                              @RequestParam(value = "roleIds", required = false) List<String> roleIds,
                              RedirectAttributes redirectAttributes) {
        try {
            if (roleIds == null || roleIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn ít nhất một vai trò");
                return "redirect:/admin/nhanvien/" + id + "/roles";
            }

            nhanVienRoleService.assignRoles(id, roleIds);
            redirectAttributes.addFlashAttribute("success",
                    "Phân quyền thành công! Đã gán " + roleIds.size() + " vai trò.");

            return "redirect:/admin/nhanvien/" + id + "/roles";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/nhanvien/" + id + "/roles";
        }
    }

    /**
     * Xóa một role khỏi nhân viên
     */
    @PostMapping("/{id}/roles/remove/{roleId}")
    public String removeRole(@PathVariable String id,
                             @PathVariable String roleId,
                             RedirectAttributes redirectAttributes) {
        try {
            nhanVienRoleService.removeRole(id, roleId);
            redirectAttributes.addFlashAttribute("success", "Đã xóa vai trò thành công");

            return "redirect:/admin/nhanvien/" + id + "/roles";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/nhanvien/" + id + "/roles";
        }
    }

    /**
     * Xóa tất cả roles của nhân viên
     */
    @PostMapping("/{id}/roles/clear")
    public String clearAllRoles(@PathVariable String id,
                                RedirectAttributes redirectAttributes) {
        try {
            nhanVienRoleService.clearAllRoles(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa tất cả vai trò");

            return "redirect:/admin/nhanvien/" + id + "/roles";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/nhanvien/" + id + "/roles";
        }
    }

    /**
     * Thay thế toàn bộ roles (xóa cũ, thêm mới)
     */
    @PostMapping("/{id}/roles/replace")
    public String replaceRoles(@PathVariable String id,
                               @RequestParam(value = "roleIds", required = false) List<String> roleIds,
                               RedirectAttributes redirectAttributes) {
        try {
            if (roleIds == null || roleIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn ít nhất một vai trò");
                return "redirect:/admin/nhanvien/" + id + "/roles";
            }

            nhanVienRoleService.replaceRoles(id, roleIds);
            redirectAttributes.addFlashAttribute("success",
                    "Đã cập nhật vai trò thành công! Hiện có " + roleIds.size() + " vai trò.");

            return "redirect:/admin/nhanvien/" + id + "/roles";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/nhanvien/" + id + "/roles";
        }
    }
}
