package com.teamforone.tech_store.controller.admin;

import com.teamforone.tech_store.dto.request.ReportDTO;
import com.teamforone.tech_store.model.User;
import com.teamforone.tech_store.service.admin.ReportService;
import com.teamforone.tech_store.service.admin.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class DashboardController {

    private final ReportService reportService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            log.info("Loading dashboard statistics...");

            // ===== DASHBOARD STATISTICS =====
            Map<String, Object> dashboardStats = reportService.getDashboardStatistics();

            // Thêm tất cả thống kê từ reportService
            model.addAttribute("totalRevenue", dashboardStats.get("formattedRevenue"));
            model.addAttribute("totalOrders", dashboardStats.get("totalOrders"));
            model.addAttribute("totalProducts", dashboardStats.get("totalProducts"));

            // ===== USER STATISTICS =====
            List<User> allUsers = userService.getAllUsers();
            long totalCustomers = allUsers.size();
            long activeUsers = userService.countActiveUsers();

            model.addAttribute("totalCustomers", totalCustomers);
            model.addAttribute("activeUsers", activeUsers);

            // ===== INVENTORY ALERTS =====
            model.addAttribute("outOfStockProducts", dashboardStats.get("outOfStockProducts"));
            model.addAttribute("lowStockProducts", dashboardStats.get("lowStockProducts"));

            // ===== TOP SELLING PRODUCTS =====
            List<ReportDTO.BestSellingProduct> bestSelling =
                    reportService.getBestSellingProducts(null, null, 5);

            List<Map<String, Object>> topProducts = bestSelling.stream()
                    .map(product -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", product.getProductName());
                        map.put("variant", product.getCategoryName() + " - " + product.getBrandName());
                        map.put("soldCount", product.getTotalSold());
                        return map;
                    })
                    .collect(Collectors.toList());

            model.addAttribute("topProducts", topProducts);

            // ===== RECENT ORDERS =====
            // Tạm thời để empty list, bạn sẽ thêm khi có OrderService
            model.addAttribute("recentOrders", new ArrayList<>());

            // ===== PAGE METADATA =====
            model.addAttribute("pageTitle", "Dashboard");

            // Breadcrumbs
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Dashboard", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            log.info("Dashboard loaded successfully");
            return "Dashboard";

        } catch (Exception e) {
            log.error("Error loading dashboard: ", e);
            model.addAttribute("errorMessage", "Lỗi khi tải Dashboard: " + e.getMessage());
            setDefaultValues(model);
            return "Dashboard";
        }
    }

    @GetMapping("/danhsach")
    public String danhsach() {
        return "redirect:/admin/products";
    }

    @GetMapping("/danhmuc")
    public String danhmuc() {
        return "redirect:/admin/categories";
    }

    @GetMapping("/thuonghieu")
    public String thuonghieu() {
        return "redirect:/admin/brands";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/import")
    public String importPage() {
        return "Import-Export";
    }

    @GetMapping("/profile")
    public String profile() {
        return "Profile";
    }

    @GetMapping("/settings")
    public String settings() {
        return "Settings";
    }

    @GetMapping("/CTProduct")
    public String ctProduct() {
        return "CTProductAdmin";
    }

    @GetMapping("/addCTProduct")
    public String addCTProduct() {
        return "AddCTProduct";
    }

    @GetMapping("/user-management")
    public String userManagement() {
        return "redirect:/admin/users";
    }

    @GetMapping("/reports-analytics")
    public String reportsAnalytics() {
        return "redirect:/admin/reports";
    }

    /**
     * Đặt giá trị mặc định khi có lỗi
     */
    private void setDefaultValues(Model model) {
        model.addAttribute("totalRevenue", "0 ₫");
        model.addAttribute("totalOrders", 0);
        model.addAttribute("totalCustomers", 0);
        model.addAttribute("totalProducts", 0);
        model.addAttribute("activeUsers", 0);
        model.addAttribute("outOfStockProducts", 0);
        model.addAttribute("lowStockProducts", 0);
        model.addAttribute("topProducts", new ArrayList<>());
        model.addAttribute("recentOrders", new ArrayList<>());
    }
}