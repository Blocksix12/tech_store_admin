//package com.teamforone.tech_store.controller.admin.crud;
//
//import com.teamforone.tech_store.dto.request.UserRequest;
//import com.teamforone.tech_store.dto.response.Response;
//import com.teamforone.tech_store.model.User;
//import com.teamforone.tech_store.service.admin.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.*;
//
//@Controller
//@RequestMapping("/admin")
//public class UserController {
//
//    private final UserService userService;
//
//    @Autowired
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    // ✅ 1️⃣ HIỂN THỊ GIAO DIỆN DANH SÁCH NGƯỜI DÙNG
//    @GetMapping("/users")
//    public String showUserList(Model model) {
//        List<User> users = userService.getAllUsers();
//        model.addAttribute("users", users);
//        model.addAttribute("title", "Quản lý người dùng");
//        model.addAttribute("pageTitle", "Quản lý người dùng");
//        model.addAttribute("activePage", "users");
//
//        // Prepare stats data
//        List<Map<String, Object>> stats = new ArrayList<>();
//
//        // Stat 1: Tổng người dùng
//        Map<String, Object> totalUsers = new HashMap<>();
//        totalUsers.put("label", "Tổng người dùng");
//        totalUsers.put("value", String.valueOf(users.size()));
//        totalUsers.put("icon", "bi bi-people-fill");
//        totalUsers.put("iconClass", "primary");
//        totalUsers.put("changeText", "+12 người dùng mới");
//        totalUsers.put("changeIcon", "bi bi-arrow-up");
//        totalUsers.put("changeClass", "positive");
//        stats.add(totalUsers);
//
//        // Stat 2: Người dùng hoạt động
//        long activeUsers = users.stream()
//                .filter(u -> "ACTIVE".equals(u.getStatus().name()))
//                .count();
//        Map<String, Object> activeUsersStat = new HashMap<>();
//        activeUsersStat.put("label", "Đang hoạt động");
//        activeUsersStat.put("value", String.valueOf(activeUsers));
//        activeUsersStat.put("icon", "bi bi-check-circle-fill");
//        activeUsersStat.put("iconClass", "success");
//        activeUsersStat.put("changeText", "85% tổng số");
//        activeUsersStat.put("changeIcon", "bi bi-arrow-up");
//        activeUsersStat.put("changeClass", "positive");
//        stats.add(activeUsersStat);
//
//        // Stat 3: Người dùng bị khóa
//        long lockedUsers = users.stream()
//                .filter(u -> "LOCKED".equals(u.getStatus().name()))
//                .count();
//        Map<String, Object> lockedUsersStat = new HashMap<>();
//        lockedUsersStat.put("label", "Bị khóa");
//        lockedUsersStat.put("value", String.valueOf(lockedUsers));
//        lockedUsersStat.put("icon", "bi bi-lock-fill");
//        lockedUsersStat.put("iconClass", "danger");
//        lockedUsersStat.put("changeText", "Cần xem xét");
//        lockedUsersStat.put("changeIcon", "bi bi-exclamation-triangle");
//        lockedUsersStat.put("changeClass", "negative");
//        stats.add(lockedUsersStat);
//
//        // Stat 4: Người dùng mới (tuần này)
//        Map<String, Object> newUsers = new HashMap<>();
//        newUsers.put("label", "Mới tuần này");
//        newUsers.put("value", "24");
//        newUsers.put("icon", "bi bi-person-plus-fill");
//        newUsers.put("iconClass", "warning");
//        newUsers.put("changeText", "+18% vs tuần trước");
//        newUsers.put("changeIcon", "bi bi-arrow-up");
//        newUsers.put("changeClass", "positive");
//        stats.add(newUsers);
//
//        model.addAttribute("stats", stats);
//
//        return "admin/quanlynguoidung";
//    }
//
//    // ✅ 2️⃣ API: LẤY USER THEO ID (cho AJAX hoặc trang edit)
//    @ResponseBody
//    @GetMapping("/users/{id}")
//    public User getUserById(@PathVariable String id) {
//        return userService.getUserById(id);
//    }
//
//    // ✅ 3️⃣ API: THÊM NGƯỜI DÙNG
//    @ResponseBody
//    @PostMapping("/users/add")
//    public ResponseEntity<Response> createUser(@RequestBody UserRequest request) {
//        Response response = userService.createUser(request);
//        return ResponseEntity.status(response.getStatus()).body(response);
//    }
//
//    // ✅ 4️⃣ API: CẬP NHẬT NGƯỜI DÙNG
//    @ResponseBody
//    @PatchMapping("/users/update/{id}")
//    public ResponseEntity<Response> updateUser(@PathVariable String id, @RequestBody UserRequest request) {
//        Response response = userService.updateUser(id, request);
//        return ResponseEntity.status(response.getStatus()).body(response);
//    }
//
//    // ✅ 5️⃣ API: XÓA NGƯỜI DÙNG
//    @ResponseBody
//    @DeleteMapping("/users/delete/{id}")
//    public ResponseEntity<Response> deleteUser(@PathVariable String id) {
//        Response response = userService.deleteUser(id);
//        return ResponseEntity.status(response.getStatus()).body(response);
//    }
//}