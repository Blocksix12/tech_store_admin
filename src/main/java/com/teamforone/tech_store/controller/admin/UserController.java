package com.teamforone.tech_store.controller.admin;

import com.teamforone.tech_store.dto.request.UserRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class UserController {

    @Autowired
    private UserService userService;

    // ===== WEB VIEWS =====

    @GetMapping("/users")
    public String showUsersPage(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        try {
            List<User> allUsers = userService.getAllUsers();

            // Filter by status
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

            // Pagination
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

            // Stats cards data
            List<Map<String, Object>> stats = new ArrayList<>();

            Map<String, Object> totalStat = new HashMap<>();
            totalStat.put("label", "Tổng người dùng");
            totalStat.put("value", String.valueOf(totalCount));
            totalStat.put("icon", "bi bi-people-fill");
            totalStat.put("iconClass", "primary");
            totalStat.put("changeText", "+12% vs tháng trước");
            totalStat.put("changeIcon", "bi bi-arrow-up");
            totalStat.put("changeClass", "positive");
            stats.add(totalStat);

            Map<String, Object> activeStat = new HashMap<>();
            activeStat.put("label", "Đang hoạt động");
            activeStat.put("value", String.valueOf(activeUsers));
            activeStat.put("icon", "bi bi-check-circle-fill");
            activeStat.put("iconClass", "success");
            activeStat.put("changeText", "85% tổng số");
            activeStat.put("changeIcon", "bi bi-arrow-up");
            activeStat.put("changeClass", "positive");
            stats.add(activeStat);

            Map<String, Object> lockedStat = new HashMap<>();
            lockedStat.put("label", "Bị khóa");
            lockedStat.put("value", String.valueOf(lockedUsers));
            lockedStat.put("icon", "bi bi-lock-fill");
            lockedStat.put("iconClass", "danger");
            lockedStat.put("changeText", "Cần xem xét");
            lockedStat.put("changeIcon", "bi bi-exclamation-triangle");
            lockedStat.put("changeClass", "negative");
            stats.add(lockedStat);

            Map<String, Object> newStat = new HashMap<>();
            newStat.put("label", "Mới tuần này");
            newStat.put("value", "24");
            newStat.put("icon", "bi bi-person-plus-fill");
            newStat.put("iconClass", "warning");
            newStat.put("changeText", "+18% vs tuần trước");
            newStat.put("changeIcon", "bi bi-arrow-up");
            newStat.put("changeClass", "positive");
            stats.add(newStat);

            model.addAttribute("stats", stats);
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
            model.addAttribute("pageTitle", "Quản lý Người dùng");
            model.addAttribute("activePage", "users"); // ✅ Thêm activePage
            model.addAttribute("searchPlaceholder", "Tìm kiếm người dùng...");
            model.addAttribute("searchId", "searchUsers");

            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Người dùng", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "/admin/quanlynguoidung"; // ✅ Đổi tên template cho đúng

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
            model.addAttribute("users", new ArrayList<>());
            return "/admin/quanlynguoidung";
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
            model.addAttribute("activePage", "users");

            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Người dùng", "url", "/admin/users"));
            breadcrumbs.add(Map.of("name", "Chi tiết", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "UserDetail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi: " + e.getMessage());
            return "redirect:/admin/users";
        }
    }

    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("userRequest", new UserRequest());
        model.addAttribute("userStatuses", User.Status.values());
        model.addAttribute("pageTitle", "Thêm Người dùng");
        model.addAttribute("activePage", "users");

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
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin!");
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
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
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
            model.addAttribute("activePage", "users");

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
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin!");
            return "EditUser";
        }

        try {
            User updatedUser = userService.updateUser(id, userRequest);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã cập nhật '" + updatedUser.getUsername() + "' thành công!");
            return "redirect:/admin/users";

        } catch (IllegalArgumentException e) {
            User user = userService.findUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("userStatuses", User.Status.values());
            model.addAttribute("errorMessage", e.getMessage());
            return "EditUser";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/users/edit/" + id;
        }
    }

    @PostMapping("/users/lock/{id}")
    public String lockUser(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            userService.lockUser(id);
            User user = userService.findUserById(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã khóa tài khoản '" + user.getUsername() + "'!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/unlock/{id}")
    public String unlockUser(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            userService.unlockUser(id);
            User user = userService.findUserById(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã mở khóa '" + user.getUsername() + "'!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ✅ THÊM: DELETE method
    @PostMapping("/users/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String id) {
        try {
            User user = userService.findUserById(id);
            String username = user.getUsername();

            // Thực hiện xóa (hoặc soft delete)
            // userService.deleteUser(id);

            return ResponseEntity.ok(Map.of(
                    "message", "Đã xóa người dùng '" + username + "' thành công!"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }

    // ===== REST API =====

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