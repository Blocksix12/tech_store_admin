package com.teamforone.tech_store.controller.admin.RBAC;

import com.teamforone.tech_store.dto.request.RoleRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.Permission;
import com.teamforone.tech_store.model.Roles;
import com.teamforone.tech_store.repository.admin.RBAC.PermissionRepository;
import com.teamforone.tech_store.service.admin.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class RoleWebController {
    private final RoleService roleService;
    private final PermissionRepository permissionRepository;

    // Hiển thị danh sách roles
    @GetMapping
    public String listRoles(Model model) {
        List<Roles> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("pageTitle", "Quản lý vai trò");
        return "RoleList";
    }

    // Hiển thị form thêm role
    @GetMapping("/add")
    public String showAddForm(Model model) {
        List<Permission> permissions = permissionRepository.findAll();
        model.addAttribute("roleRequest", new RoleRequest());
        model.addAttribute("permissions", permissions);
        model.addAttribute("roleNames", Roles.RoleName.values());
        model.addAttribute("pageTitle", "Thêm vai trò mới");
        return "AddRoles";
    }

    // Xử lý thêm role
    @PostMapping("/add")
    public String addRole(@ModelAttribute RoleRequest roleRequest,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ");
            return "redirect:/admin/web/roles/add";
        }

        try {
            Response response = roleService.createRole(roleRequest);
            if (response.getStatus() == 200) {
                redirectAttributes.addFlashAttribute("success", "Thêm vai trò thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", response.getMessage());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/web/roles";
    }

    // Hiển thị chi tiết role
    @GetMapping("/detail/{id}")
    public String viewDetail(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Roles role = roleService.getAllRoles().stream()
                    .filter(r -> r.getRoleID().equals(id))
                    .findFirst()
                    .orElse(null);

            if (role == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy vai trò");
                return "redirect:/admin/roles";
            }

            model.addAttribute("role", role);
            model.addAttribute("pageTitle", "Chi tiết vai trò");
            return "RoleDetail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/roles";
        }
    }

    // Xác nhận xóa role
    @GetMapping("/delete/{id}")
    public String confirmDelete(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Roles role = roleService.getAllRoles().stream()
                    .filter(r -> r.getRoleID().equals(id))
                    .findFirst()
                    .orElse(null);

            if (role == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy vai trò");
                return "redirect:/admin/roles";
            }

            model.addAttribute("role", role);
            model.addAttribute("pageTitle", "Xác nhận xóa vai trò");
            return "DeleteConfirm";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/roles";
        }
    }

    // Xử lý xóa role
    @PostMapping("/delete/{id}")
    public String deleteRole(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            Response response = roleService.deleteRole(id);
            if (response.getStatus() == 200) {
                redirectAttributes.addFlashAttribute("success", "Xóa vai trò thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", response.getMessage());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/roles";
    }
}
