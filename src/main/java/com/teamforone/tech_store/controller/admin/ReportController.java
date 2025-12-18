// ========================================
// FILE 2: ReportController.java - UPDATED
// ========================================
package com.teamforone.tech_store.controller.admin;

import com.teamforone.tech_store.dto.request.ReportDTO;
import com.teamforone.tech_store.dto.response.CustomerReportDTO;
import com.teamforone.tech_store.service.admin.CustomerReportService;
import com.teamforone.tech_store.service.admin.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final CustomerReportService customerReportService;

    // ========== WEB VIEWS ==========

    @GetMapping("/reports")
    public String showReportsPage(Model model) {
        try {
            Map<String, Object> dashboardStats = reportService.getDashboardStatistics();
            model.addAttribute("dashboardStats", dashboardStats);
            model.addAttribute("pageTitle", "Báo cáo & Thống kê");

            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Báo cáo", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "Reports";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "Reports";
        }
    }

    @GetMapping("/reports/best-selling")
    public String showBestSellingProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit,
            Model model) {
        try {
            List<ReportDTO.BestSellingProduct> products =
                    reportService.getBestSellingProducts(startDate, endDate, limit);

            model.addAttribute("products", products);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("limit", limit);
            model.addAttribute("pageTitle", "Sản phẩm Bán chạy");

            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Báo cáo", "url", "/admin/reports"));
            breadcrumbs.add(Map.of("name", "Sản phẩm bán chạy", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "BestSellingReport";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "BestSellingReport";
        }
    }

    @GetMapping("/reports/inventory")
    public String showInventoryReport(
            @RequestParam(required = false) String status,
            Model model) {
        try {
            List<ReportDTO.InventoryReport> inventory = reportService.getInventoryReport();

            if (status != null && !status.isEmpty()) {
                inventory = inventory.stream()
                        .filter(i -> {
                            switch (status) {
                                case "out": return i.getTotalStock() == 0;
                                case "low": return i.getTotalStock() > 0 && i.getTotalStock() <= 10;
                                case "in": return i.getTotalStock() > 10;
                                default: return true;
                            }
                        })
                        .toList();
            }

            long totalProducts = inventory.size();
            long outOfStock = inventory.stream().filter(i -> i.getTotalStock() == 0).count();
            long lowStock = inventory.stream()
                    .filter(i -> i.getTotalStock() > 0 && i.getTotalStock() <= 10).count();
            long inStock = totalProducts - outOfStock - lowStock;

            model.addAttribute("inventory", inventory);
            model.addAttribute("totalProducts", totalProducts);
            model.addAttribute("outOfStock", outOfStock);
            model.addAttribute("lowStock", lowStock);
            model.addAttribute("inStock", inStock);
            model.addAttribute("pageTitle", "Báo cáo Tồn kho");

            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Báo cáo", "url", "/admin/reports"));
            breadcrumbs.add(Map.of("name", "Tồn kho", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "InventoryReport";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "InventoryReport";
        }
    }

    @GetMapping("/reports/revenue")
    public String showRevenueReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "month") String groupBy,
            Model model) {
        try {
            List<ReportDTO.RevenueReport> revenue =
                    reportService.getRevenueReport(startDate, endDate, groupBy);

            double totalRevenue = revenue.stream()
                    .mapToDouble(ReportDTO.RevenueReport::getTotalRevenue).sum();
            long totalOrders = revenue.stream()
                    .mapToLong(ReportDTO.RevenueReport::getTotalOrders).sum();

            model.addAttribute("revenue", revenue);
            model.addAttribute("totalRevenue", totalRevenue);
            model.addAttribute("totalOrders", totalOrders);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("groupBy", groupBy);
            model.addAttribute("pageTitle", "Báo cáo Doanh thu");

            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Báo cáo", "url", "/admin/reports"));
            breadcrumbs.add(Map.of("name", "Doanh thu", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "RevenueReport";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "RevenueReport";
        }
    }

    @GetMapping("/reports/category-performance")
    public String showCategoryPerformance(Model model) {
        try {
            List<ReportDTO.CategoryPerformance> performance = reportService.getCategoryPerformance();

            model.addAttribute("categories", performance); // ✅ Đổi tên từ "performance" thành "categories"
            model.addAttribute("pageTitle", "Hiệu suất Danh mục");

            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Báo cáo", "url", "/admin/reports"));
            breadcrumbs.add(Map.of("name", "Hiệu suất danh mục", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "CategoryPerformanceReport";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "CategoryPerformanceReport";
        }
    }

    @GetMapping("/reports/brand-performance")
    public String showBrandPerformance(Model model) {
        try {
            List<ReportDTO.BrandPerformance> performance = reportService.getBrandPerformance();

            model.addAttribute("brands", performance); // ✅ Đổi tên từ "performance" thành "brands"
            model.addAttribute("pageTitle", "Hiệu suất Thương hiệu");

            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Báo cáo", "url", "/admin/reports"));
            breadcrumbs.add(Map.of("name", "Hiệu suất thương hiệu", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "BrandPerformanceReport";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "BrandPerformanceReport";
        }
    }


    @GetMapping("/reports/customers")
    public String showCustomerReport(Model model) {
        try {
            // Lấy thống kê
            Map<String, Object> stats = customerReportService.getCustomerStatistics();
            model.addAttribute("totalCustomers", stats.get("totalCustomers"));
            model.addAttribute("vipCustomers", stats.get("vipCustomers"));
            model.addAttribute("newCustomers", stats.get("newCustomers"));
            model.addAttribute("returnRate", stats.get("returnRate"));

            // Lấy top 10 khách hàng
            List<CustomerReportDTO> topCustomers = customerReportService.getTopCustomers(10);
            model.addAttribute("topCustomers", topCustomers);

            // Breadcrumbs
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("name", "Trang chủ", "url", "/admin"));
            breadcrumbs.add(Map.of("name", "Báo cáo", "url", "/admin/reports"));
            breadcrumbs.add(Map.of("name", "Khách hàng", "url", ""));
            model.addAttribute("breadcrumbs", breadcrumbs);

            model.addAttribute("pageTitle", "Báo cáo Khách hàng");
            model.addAttribute("activePage", "reports");

            return "CustomerReport";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
            model.addAttribute("topCustomers", new ArrayList<>());
            return "CustomerReport";
        }
    }

    // ========== API ENDPOINTS FOR AJAX ==========

    @GetMapping("/api/reports/dashboard")
    @ResponseBody
    public Map<String, Object> getDashboardData() {
        return reportService.getDashboardStatistics();
    }

    @GetMapping("/api/reports/revenue-chart")
    @ResponseBody
    public List<ReportDTO.RevenueReport> getRevenueChartData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "month") String groupBy) {
        return reportService.getRevenueReport(startDate, endDate, groupBy);
    }

    // ========== EXCEL EXPORTS ==========

    @GetMapping("/reports/best-selling/export")
    public void exportBestSelling(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "50") int limit,
            HttpServletResponse response) throws IOException {

        List<ReportDTO.BestSellingProduct> products =
                reportService.getBestSellingProducts(startDate, endDate, limit);
        reportService.exportBestSellingToExcel(products, response);
    }

    @GetMapping("/reports/inventory/export")
    public void exportInventory(HttpServletResponse response) throws IOException {
        List<ReportDTO.InventoryReport> inventory = reportService.getInventoryReport();
        reportService.exportInventoryToExcel(inventory, response);
    }

    @GetMapping("/reports/revenue/export")
    public void exportRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "month") String groupBy,
            HttpServletResponse response) throws IOException {

        List<ReportDTO.RevenueReport> revenue =
                reportService.getRevenueReport(startDate, endDate, groupBy);
        reportService.exportRevenueToExcel(revenue, groupBy, response);
    }

    @GetMapping("/reports/category-performance/export")
    public void exportCategoryPerformance(HttpServletResponse response) throws IOException {
        List<ReportDTO.CategoryPerformance> performance = reportService.getCategoryPerformance();
        reportService.exportCategoryPerformanceToExcel(performance, response);
    }

    @GetMapping("/reports/brand-performance/export")
    public void exportBrandPerformance(HttpServletResponse response) throws IOException {
        List<ReportDTO.BrandPerformance> performance = reportService.getBrandPerformance();
        reportService.exportBrandPerformanceToExcel(performance, response);
    }

    @GetMapping("/reports/comprehensive/export")
    public void exportComprehensive(HttpServletResponse response) throws IOException {
        reportService.exportComprehensiveReport(response);
    }

    @GetMapping("/reports/customers/export")
    public void exportCustomerReport(HttpServletResponse response) throws IOException {
        // Implementation sau
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=customer_report.xlsx");
        // ...
    }
}