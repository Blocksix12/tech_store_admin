package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.ReportDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportService {

    /**
     * Dashboard tổng quan
     */
    Map<String, Object> getDashboardStatistics();

    /**
     * Sản phẩm bán chạy nhất
     */
    List<ReportDTO.BestSellingProduct> getBestSellingProducts(
            LocalDate startDate, LocalDate endDate, int limit);

    /**
     * Báo cáo tồn kho
     */
    List<ReportDTO.InventoryReport> getInventoryReport();

    /**
     * Báo cáo doanh thu theo thời gian
     * @param groupBy: "day", "week", "month", "year"
     */
    List<ReportDTO.RevenueReport> getRevenueReport(
            LocalDate startDate, LocalDate endDate, String groupBy);

    /**
     * Hiệu suất theo danh mục
     */
    List<ReportDTO.CategoryPerformance> getCategoryPerformance();

    /**
     * Hiệu suất theo thương hiệu
     */
    List<ReportDTO.BrandPerformance> getBrandPerformance();

    // ===== EXCEL EXPORT METHODS =====

    /**
     * Xuất báo cáo sản phẩm bán chạy ra Excel
     */
    void exportBestSellingToExcel(List<ReportDTO.BestSellingProduct> products,
                                  HttpServletResponse response) throws IOException;

    /**
     * Xuất báo cáo tồn kho ra Excel
     */
    void exportInventoryToExcel(List<ReportDTO.InventoryReport> inventory,
                                HttpServletResponse response) throws IOException;

    /**
     * Xuất báo cáo doanh thu ra Excel
     */
    void exportRevenueToExcel(List<ReportDTO.RevenueReport> revenue,
                              String groupBy,
                              HttpServletResponse response) throws IOException;

    /**
     * Xuất báo cáo hiệu suất danh mục ra Excel
     */
    void exportCategoryPerformanceToExcel(List<ReportDTO.CategoryPerformance> performance,
                                          HttpServletResponse response) throws IOException;

    /**
     * Xuất báo cáo hiệu suất thương hiệu ra Excel
     */
    void exportBrandPerformanceToExcel(List<ReportDTO.BrandPerformance> performance,
                                       HttpServletResponse response) throws IOException;

    /**
     * Xuất báo cáo tổng hợp (tất cả báo cáo trong 1 file Excel)
     */
    void exportComprehensiveReport(HttpServletResponse response) throws IOException;
}