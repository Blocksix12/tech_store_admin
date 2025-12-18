package com.teamforone.tech_store.controller.admin;

import com.teamforone.tech_store.dto.request.NhanVienUpdateRequest;
import com.teamforone.tech_store.dto.request.RegisterRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.NhanVien;
import com.teamforone.tech_store.model.Roles;
import com.teamforone.tech_store.repository.admin.RBAC.NhanVienRepository;
import com.teamforone.tech_store.service.admin.NhanVienService;
import com.teamforone.tech_store.service.admin.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/nhanvien")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class NhanVienWebController {
    private final NhanVienService nhanVienService;
    private final NhanVienRepository nhanVienRepository;
    private final RoleService roleService;

    /**
     * Hiển thị danh sách nhân viên
     */
    @GetMapping
    public String listNhanVien(Model model) {
        List<NhanVien> nhanViens = nhanVienRepository.findAll();
        long activeCount = 0;
        long lockedCount = 0;
        long adminCount = 0;

        if (nhanViens != null) {
            activeCount = nhanViens.stream()
                    .filter(nv -> nv.getStatus() == NhanVien.Status.ACTIVE)
                    .count();

            lockedCount = nhanViens.stream()
                    .filter(nv -> nv.getStatus() == NhanVien.Status.LOCKED)
                    .count();

            adminCount = nhanViens.stream()
                    .filter(nv -> nv.isAdmin()) // giả sử NhanVien có phương thức isAdmin()
                    .count();
        }
        model.addAttribute("nhanViens", nhanViens);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("lockedCount", lockedCount);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("pageTitle", "Danh sách nhân viên");
        return "NhanVienList";
    }

    /**
     * Hiển thị form thêm nhân viên
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        List<Roles> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("pageTitle", "Thêm nhân viên mới");
        return "AddNhanVien";
    }

    /**
     * Xử lý thêm nhân viên
     */
    @PostMapping("/add")
    public String addNhanVien(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam("fullName") String fullName,
                              @RequestParam("email") String email,
                              @RequestParam("phoneNumber") String phoneNumber,
                              @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                              @RequestParam(value = "dateOfBirth", required = false) String dateOfBirthStr,
                              @RequestParam(value = "gender", required = false) String gender,
                              @RequestParam(value = "address", required = false) String address,
                              @RequestParam(value = "bio", required = false) String bio,
                              @RequestParam(value = "website", required = false) String website,
                              @RequestParam(value = "active", defaultValue = "true") boolean active,
                              @RequestParam(value = "roles", required = false) List<String> roleIds,
                              RedirectAttributes redirectAttributes) {
        try {
            // Parse date of birth
            Date dateOfBirth = null;
            if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    dateOfBirth = sdf.parse(dateOfBirthStr);
                } catch (Exception e) {
                    // Ignore parse error
                }
            }

            // Tạo RegisterRequest
            RegisterRequest request = RegisterRequest.builder()
                    .username(username.trim())
                    .password(password)
                    .fullName(fullName.trim())
                    .email(email.trim().toLowerCase())
                    .phoneNumber(phoneNumber.trim())
                    .avatarFile(avatarFile)
                    .build();

            // Tạo nhân viên
            Response response = nhanVienService.createNhanVien(request);

            if (response.getStatus() != 200) {
                redirectAttributes.addFlashAttribute("error", response.getMessage());
                return "redirect:/admin/nhanvien/add";
            }

            // Lấy nhân viên vừa tạo để cập nhật thêm thông tin
            NhanVien nhanVien = nhanVienRepository.findByUsername(username).orElse(null);
            if (nhanVien != null) {
                // Cập nhật thông tin bổ sung
                if (dateOfBirth != null) nhanVien.setDateOfBirth(dateOfBirth);
                if (gender != null && !gender.isEmpty()) {
                    try {
                        nhanVien.setGender(com.teamforone.tech_store.enums.Gender.valueOf(gender));
                    } catch (IllegalArgumentException ignored) {}
                }
                if (address != null && !address.isEmpty()) nhanVien.setAddress(address.trim());
                if (bio != null && !bio.isEmpty()) nhanVien.setBio(bio.trim());
                if (website != null && !website.isEmpty()) nhanVien.setWebsite(website.trim());
                nhanVien.setStatus(active ? NhanVien.Status.ACTIVE : NhanVien.Status.LOCKED);

                // Gán roles
                if (roleIds != null && !roleIds.isEmpty()) {
                    var roles = roleService.getAllRoles().stream()
                            .filter(role -> roleIds.contains(role.getRoleID()))
                            .collect(java.util.stream.Collectors.toSet());
                    nhanVien.setRoles(roles);
                }

                nhanVienRepository.save(nhanVien);
            }

            redirectAttributes.addFlashAttribute("success", "Thêm nhân viên thành công!");
            return "redirect:/admin/nhanvien";

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi upload ảnh: " + e.getMessage());
            return "redirect:/admin/nhanvien/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/nhanvien/add";
        }
    }

    /**
     * Hiển thị chi tiết nhân viên
     */
    @GetMapping("/detail/{id}")
    public String viewDetail(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NhanVien nhanVien = nhanVienService.findNhanVienById(id);
            if (nhanVien == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhân viên");
                return "redirect:/admin/nhanvien";
            }

            model.addAttribute("nhanVien", nhanVien);
            model.addAttribute("pageTitle", "Chi tiết nhân viên");
            return "NhanVienDetail";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/nhanvien";
        }
    }

    /**
     * Hiển thị form cập nhật nhân viên
     */
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NhanVien nhanVien = nhanVienService.findNhanVienById(id);
            if (nhanVien == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhân viên");
                return "redirect:/admin/nhanvien";
            }

            Set<String> nhanVienRoleIds = new HashSet<>();
            if (nhanVien.getRoles() != null) {
                nhanVienRoleIds = nhanVien.getRoles().stream()
                        .map(Roles::getRoleID)
                        .collect(Collectors.toSet());
            }
            model.addAttribute("nhanVienRoleIds", nhanVienRoleIds);


            List<Roles> roles = roleService.getAllRoles();
            model.addAttribute("nhanVien", nhanVien);
            model.addAttribute("roles", roles);
            model.addAttribute("pageTitle", "Cập nhật nhân viên");
            return "UpdateNhanVien";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/nhanvien";
        }
    }

    /**
     * Xử lý cập nhật nhân viên
     */
    @PostMapping("/update/{id}")
    public String updateNhanVien(@PathVariable String id,
                                 @ModelAttribute NhanVienUpdateRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            Response response = nhanVienService.updateNhanVien(id, request);

            if (response.getStatus() == 200) {
                redirectAttributes.addFlashAttribute("success", "Cập nhật nhân viên thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", response.getMessage());
            }

            return "redirect:/admin/nhanvien";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/nhanvien/update/" + id;
        }
    }

    /**
     * Hiển thị trang xác nhận xóa
     */
    @GetMapping("/delete/{id}")
    public String confirmDelete(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NhanVien nhanVien = nhanVienService.findNhanVienById(id);
            if (nhanVien == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhân viên");
                return "redirect:/admin/nhanvien";
            }

            model.addAttribute("nhanVien", nhanVien);
            model.addAttribute("pageTitle", "Xác nhận xóa nhân viên");
            return "DeleteNhanVien";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/nhanvien";
        }
    }

    /**
     * Xử lý xóa nhân viên
     */
    @PostMapping("/delete/{id}")
    public String deleteNhanVien(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            NhanVien deleted = nhanVienService.deleteNhanVien(id);

            if (deleted != null) {
                redirectAttributes.addFlashAttribute("success", "Xóa nhân viên thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhân viên để xóa");
            }

            return "redirect:/admin/nhanvien";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/nhanvien";
        }
    }
}
