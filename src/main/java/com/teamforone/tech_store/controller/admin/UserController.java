package com.teamforone.tech_store.controller.admin;

import com.teamforone.tech_store.dto.request.UserRequest;
import com.teamforone.tech_store.dto.response.Response;
import com.teamforone.tech_store.model.User;
import com.teamforone.tech_store.service.admin.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class UserController {
    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ===== WEB VIEWS =====

    @GetMapping("/users")
    public String showUsersPage(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        try {
            List<User> allUsers = userService.getAllUsers();

            // Filter by status if provided
            if (status != null && !status.isEmpty()) {
                try {
                    User.Status userStatus = User.Status.valueOf(status.toUpperCase());
                    allUsers = allUsers.stream()
                            .filter(u -> u.getStatus() == userStatus)
                            .toList();
                } catch (IllegalArgumentException e) {
                    // Keep all users
                }
            }

            // Calculate pagination
            int totalUsers = allUsers.size();
            int totalPages = (int) Math.ceil((double) totalUsers / size);

            if (page < 1) page = 1;
            if (page > totalPages && totalPages > 0) page = totalPages;

            int startIndex = (page - 1) * size;
            int endIndex = Math.min(startIndex + size, totalUsers);

            List<User> users = allUsers.subList(
                    Math.min(startIndex, totalUsers),
                    endIndex
            );

            // Statistics
            long totalCount = totalUsers;
            long activeUsers = allUsers.stream()
                    .filter(u -> u.getStatus() == User.Status.ACTIVE)
                    .count();
            long lockedUsers = totalCount - activeUsers;

            // Add to model
            model.addAttribute("users", users);
            model.addAttribute("totalUsers", totalCount);
            model.addAttribute("activeUsers", activeUsers);
            model.addAttribute("lockedUsers", lockedUsers);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", size);
            model.addAttribute("startIndex", startIndex + 1);
            model.addAttribute("endIndex", endIndex);
            model.addAttribute("userStatuses", User.Status.values());

            // Data for topbar
            model.addAttribute("pageTitle", "Quản lý Người dùng");
            model.addAttribute("searchPlaceholder", "Tìm kiếm người dùng...");
            model.addAttribute("searchId", "searchUsers");

            // Breadcrumbs
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Người dùng", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "Users";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tải danh sách người dùng: " + e.getMessage());
            model.addAttribute("users", new ArrayList<>());
            return "Users";
        }
    }

    @GetMapping("/users/detail/{id}")
    public String showUserDetail(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findUserById(id);

            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
                return "redirect:/admin/users";
            }

            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Chi tiết Người dùng");

            // Breadcrumbs
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Người dùng", "url", "/admin/users"));
            breadcrumbs.add(Map.of("name", "Chi tiết", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "UserDetail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/users";
        }
    }

    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("userRequest", new UserRequest());
        model.addAttribute("userStatuses", User.Status.values());
        model.addAttribute("pageTitle", "Thêm Người dùng");

        // Breadcrumbs
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
        breadcrumbs.add(Map.of("name", "Người dùng", "url", "/admin/users"));
        breadcrumbs.add(Map.of("name", "Thêm mới", "url", ""));
        model.addAttribute("breadcrumbs", breadcrumbs);

        return "AddUser";
    }

    @PostMapping("/users/add")
    public String addUser(
            @Valid @ModelAttribute("userRequest") UserRequest userRequest,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("userStatuses", User.Status.values());
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin đã nhập!");
            return "AddUser";
        }

        try {
            User savedUser = userService.addUser(userRequest);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã thêm người dùng '" + savedUser.getUsername() + "' thành công!");
            return "redirect:/admin/users";

        } catch (IllegalArgumentException e) {
            model.addAttribute("userStatuses", User.Status.values());
            model.addAttribute("errorMessage", e.getMessage());
            return "AddUser";

        } catch (Exception e) {
            model.addAttribute("userStatuses", User.Status.values());
            model.addAttribute("errorMessage", "Lỗi khi thêm người dùng: " + e.getMessage());
            return "AddUser";
        }
    }

    @GetMapping("/users/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findUserById(id);

            UserRequest userRequest = UserRequest.builder()
                    .username(user.getUsername())
                    .fullname(user.getFullname())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .status(user.getStatus().name())
                    .build();

            model.addAttribute("user", user);
            model.addAttribute("userRequest", userRequest);
            model.addAttribute("userStatuses", User.Status.values());
            model.addAttribute("pageTitle", "Chỉnh sửa Người dùng");

            // Breadcrumbs
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Người dùng", "url", "/admin/users"));
            breadcrumbs.add(Map.of("name", "Chỉnh sửa", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "EditUser";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(
            @PathVariable String id,
            @Valid @ModelAttribute("userRequest") UserRequest userRequest,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            User user = userService.findUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("userStatuses", User.Status.values());
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin đã nhập!");
            return "EditUser";
        }

        try {
            User updatedUser = userService.updateUser(id, userRequest);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã cập nhật người dùng '" + updatedUser.getUsername() + "' thành công!");
            return "redirect:/admin/users";

        } catch (IllegalArgumentException e) {
            User user = userService.findUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("userStatuses", User.Status.values());
            model.addAttribute("errorMessage", e.getMessage());
            return "EditUser";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Lỗi khi cập nhật người dùng: " + e.getMessage());
            return "redirect:/admin/users/edit/" + id;
        }
    }

    @PostMapping("/users/lock/{id}")
    public String lockUser(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            userService.lockUser(id);
            User user = userService.findUserById(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã khóa tài khoản '" + user.getUsername() + "' thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Lỗi khi khóa tài khoản: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/unlock/{id}")
    public String unlockUser(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            userService.unlockUser(id);
            User user = userService.findUserById(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã mở khóa tài khoản '" + user.getUsername() + "' thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Lỗi khi mở khóa tài khoản: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ===== REST API ENDPOINTS =====

    @GetMapping("/api/users")
    @ResponseBody
    public ResponseEntity<List<User>> getAllUsersApi() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }
}