package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.dto.request.ReportDTO;
import com.teamforone.tech_store.repository.admin.ReportRepository;
import com.teamforone.tech_store.repository.admin.UserRepository;
import com.teamforone.tech_store.repository.admin.crud.ProductRepository;
import com.teamforone.tech_store.service.admin.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private final ReportRepository reportRepository;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final UserRepository userRepository;

    public ReportServiceImpl(ReportRepository reportRepository,
                             ProductRepository productRepository,
                             UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Total products
            Long totalProducts = productRepository.count();
            stats.put("totalProducts", totalProducts);

            // Total users
            Long totalUsers = userRepository.count();
            stats.put("totalUsers", totalUsers);

            // ✅ FIX: Orders & Revenue - Xử lý đúng Object[]
            Object[] orderStats = reportRepository.getTotalOrdersAndRevenue();

            Long totalOrders = 0L;
            Double totalRevenue = 0.0;

            if (orderStats != null && orderStats.length >= 2) {
                // ✅ Cast từng element, KHÔNG cast cả array
                if (orderStats[0] != null) {
                    totalOrders = ((Number) orderStats[0]).longValue();
                }
                if (orderStats[1] != null) {
                    totalRevenue = ((Number) orderStats[1]).doubleValue();
                }
            }

            stats.put("totalOrders", totalOrders);
            stats.put("totalRevenue", totalRevenue);
            stats.put("formattedRevenue", formatCurrency(totalRevenue));

            // ✅ FIX: Stock status
            List<Object[]> stockData = reportRepository.getStockStatus();
            Map<String, Long> stockStatus = new HashMap<>();

            for (Object[] row : stockData) {
                if (row != null && row.length >= 2) {
                    String status = row[0] != null ? row[0].toString() : "";
                    Long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
                    stockStatus.put(status, count);
                }
            }

            stats.put("lowStockProducts", stockStatus.getOrDefault("LOW_STOCK", 0L));
            stats.put("outOfStockProducts", stockStatus.getOrDefault("OUT_OF_STOCK", 0L));

            // User statistics
            stats.put("activeUsers", userRepository.countByStatus(com.teamforone.tech_store.model.User.Status.ACTIVE));
            stats.put("lockedUsers", userRepository.countByStatus(com.teamforone.tech_store.model.User.Status.LOCKED));

            // ✅ FIX: Today's orders and revenue
            Object[] todayStats = reportRepository.getTodayOrdersAndRevenue();

            Long todayOrders = 0L;
            Double todayRevenue = 0.0;

            if (todayStats != null && todayStats.length >= 2) {
                if (todayStats[0] != null) {
                    todayOrders = ((Number) todayStats[0]).longValue();
                }
                if (todayStats[1] != null) {
                    todayRevenue = ((Number) todayStats[1]).doubleValue();
                }
            }

            stats.put("todayOrders", todayOrders);
            stats.put("todayRevenue", todayRevenue);
            stats.put("formattedTodayRevenue", formatCurrency(todayRevenue));

        } catch (Exception e) {
            // ✅ Log chi tiết lỗi
            System.err.println("❌ Error in getDashboardStatistics: " + e.getMessage());
            e.printStackTrace();

            // ✅ Trả về giá trị mặc định khi có lỗi
            stats.put("totalProducts", 0L);
            stats.put("totalUsers", 0L);
            stats.put("totalOrders", 0L);
            stats.put("totalRevenue", 0.0);
            stats.put("formattedRevenue", "0 ₫");
            stats.put("lowStockProducts", 0L);
            stats.put("outOfStockProducts", 0L);
            stats.put("activeUsers", 0L);
            stats.put("lockedUsers", 0L);
            stats.put("todayOrders", 0L);
            stats.put("todayRevenue", 0.0);
            stats.put("formattedTodayRevenue", "0 ₫");
        }

        return stats;
    }

    private String formatCurrency(Double amount) {
        if (amount == null || amount == 0) {
            return "0 ₫";
        }
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " ₫";
    }

    @Override
    public List<ReportDTO.BestSellingProduct> getBestSellingProducts(
            LocalDate startDate, LocalDate endDate, int limit) {

        List<Object[]> results = reportRepository.getBestSellingProducts(startDate, endDate, limit);

        return results.stream()
                .map(this::mapToBestSellingProduct)
                .collect(Collectors.toList());
    }

    private ReportDTO.BestSellingProduct mapToBestSellingProduct(Object[] row) {
        String productId = row[0] != null ? row[0].toString() : "";
        String productName = row[1] != null ? row[1].toString() : "";
        String productImage = row[2] != null ? row[2].toString() : "";
        String categoryName = row[3] != null ? row[3].toString() : "Chưa phân loại";
        String brandName = row[4] != null ? row[4].toString() : "Chưa có thương hiệu";
        Long totalSold = row[5] != null ? ((Number) row[5]).longValue() : 0L;
        Double totalRevenue = row[6] != null ? ((Number) row[6]).doubleValue() : 0.0;
        Double averagePrice = totalSold > 0 ? totalRevenue / totalSold : 0.0;

        return ReportDTO.BestSellingProduct.builder()
                .productId(productId)
                .productName(productName)
                .productImage(productImage)
                .categoryName(categoryName)
                .brandName(brandName)
                .totalSold(totalSold)
                .totalRevenue(totalRevenue)
                .formattedRevenue(formatCurrency(totalRevenue))
                .averagePrice(averagePrice)
                .build();
    }

    @Override
    public List<ReportDTO.InventoryReport> getInventoryReport() {
        List<Object[]> results = reportRepository.getInventoryReport();

        return results.stream()
                .map(this::mapToInventoryReport)
                .collect(Collectors.toList());
    }

    private ReportDTO.InventoryReport mapToInventoryReport(Object[] row) {
        String productId = row[0] != null ? row[0].toString() : "";
        String productName = row[1] != null ? row[1].toString() : "";
        String productImage = row[2] != null ? row[2].toString() : "";
        String categoryName = row[3] != null ? row[3].toString() : "Chưa phân loại";
        String brandName = row[4] != null ? row[4].toString() : "Chưa có thương hiệu";
        Long totalStock = row[5] != null ? ((Number) row[5]).longValue() : 0L;
        Long variantCount = row[6] != null ? ((Number) row[6]).longValue() : 0L;

        String stockStatus;
        String stockStatusText;
        if (totalStock == 0) {
            stockStatus = "OUT_OF_STOCK";
            stockStatusText = "Hết hàng";
        } else if (totalStock <= 10) {
            stockStatus = "LOW_STOCK";
            stockStatusText = "Sắp hết";
        } else {
            stockStatus = "IN_STOCK";
            stockStatusText = "Còn hàng";
        }

        return ReportDTO.InventoryReport.builder()
                .productId(productId)
                .productName(productName)
                .productImage(productImage)
                .categoryName(categoryName)
                .brandName(brandName)
                .totalStock(totalStock)
                .variantCount(variantCount)
                .stockStatus(stockStatus)
                .stockStatusText(stockStatusText)
                .build();
    }

    @Override
    public List<ReportDTO.RevenueReport> getRevenueReport(
            LocalDate startDate, LocalDate endDate, String groupBy) {

        List<Object[]> results;
        switch (groupBy.toLowerCase()) {
            case "day":
                results = reportRepository.getRevenueByDay(startDate, endDate);
                break;
            case "week":
                results = reportRepository.getRevenueByWeek(startDate, endDate);
                break;
            case "year":
                results = reportRepository.getRevenueByYear(startDate, endDate);
                break;
            case "month":
            default:
                results = reportRepository.getRevenueByMonth(startDate, endDate);
                break;
        }

        return results.stream()
                .map(this::mapToRevenueReport)
                .collect(Collectors.toList());
    }

    private ReportDTO.RevenueReport mapToRevenueReport(Object[] row) {
        String period = row[0] != null ? row[0].toString() : "";
        Double totalRevenue = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
        Long totalOrders = row[2] != null ? ((Number) row[2]).longValue() : 0L;
        Double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;

        return ReportDTO.RevenueReport.builder()
                .period(period)
                .totalRevenue(totalRevenue)
                .formattedRevenue(formatCurrency(totalRevenue))
                .totalOrders(totalOrders)
                .averageOrderValue(averageOrderValue)
                .build();
    }

    @Override
    public List<ReportDTO.CategoryPerformance> getCategoryPerformance() {
        List<Object[]> results = reportRepository.getCategoryPerformance();

        // Calculate total revenue for market share
        double totalRevenue = results.stream()
                .mapToDouble(row -> row[4] != null ? ((Number) row[4]).doubleValue() : 0.0)
                .sum();

        return results.stream()
                .map(row -> mapToCategoryPerformance(row, totalRevenue))
                .collect(Collectors.toList());
    }

    private ReportDTO.CategoryPerformance mapToCategoryPerformance(Object[] row, double totalRevenue) {
        String categoryId = row[0] != null ? row[0].toString() : "";
        String categoryName = row[1] != null ? row[1].toString() : "";
        Long productCount = row[2] != null ? ((Number) row[2]).longValue() : 0L;
        Long totalSold = row[3] != null ? ((Number) row[3]).longValue() : 0L;
        Double revenue = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;
        Double marketShare = totalRevenue > 0 ? (revenue / totalRevenue) * 100 : 0.0;

        return ReportDTO.CategoryPerformance.builder()
                .categoryId(categoryId)
                .categoryName(categoryName)
                .productCount(productCount)
                .totalSold(totalSold)
                .totalRevenue(revenue)
                .formattedRevenue(formatCurrency(revenue))
                .marketShare(marketShare)
                .build();
    }

    @Override
    public List<ReportDTO.BrandPerformance> getBrandPerformance() {
        List<Object[]> results = reportRepository.getBrandPerformance();

        // Calculate total revenue for market share
        double totalRevenue = results.stream()
                .mapToDouble(row -> row[5] != null ? ((Number) row[5]).doubleValue() : 0.0)
                .sum();

        return results.stream()
                .map(row -> mapToBrandPerformance(row, totalRevenue))
                .collect(Collectors.toList());
    }

    private ReportDTO.BrandPerformance mapToBrandPerformance(Object[] row, double totalRevenue) {
        String brandId = row[0] != null ? row[0].toString() : "";
        String brandName = row[1] != null ? row[1].toString() : "";
        String logoUrl = row[2] != null ? row[2].toString() : "";
        Long productCount = row[3] != null ? ((Number) row[3]).longValue() : 0L;
        Long totalSold = row[4] != null ? ((Number) row[4]).longValue() : 0L;
        Double revenue = row[5] != null ? ((Number) row[5]).doubleValue() : 0.0;
        Double marketShare = totalRevenue > 0 ? (revenue / totalRevenue) * 100 : 0.0;

        return ReportDTO.BrandPerformance.builder()
                .brandId(brandId)
                .brandName(brandName)
                .logoUrl(logoUrl)
                .productCount(productCount)
                .totalSold(totalSold)
                .totalRevenue(revenue)
                .formattedRevenue(formatCurrency(revenue))
                .marketShare(marketShare)
                .build();
    }

    // ===== EXCEL EXPORT IMPLEMENTATIONS =====

    @Override
    public void exportBestSellingToExcel(List<ReportDTO.BestSellingProduct> products,
                                         HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=best_selling_products_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sản phẩm bán chạy");

            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle normalStyle = createNormalStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle priceStyle = createPriceStyle(workbook);

            // Header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"STT", "Tên sản phẩm", "Danh mục", "Thương hiệu",
                    "Số lượng bán", "Doanh thu", "Giá trung bình"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowIdx = 1;
            for (ReportDTO.BestSellingProduct product : products) {
                Row row = sheet.createRow(rowIdx);

                createCell(row, 0, rowIdx, numberStyle);
                createCell(row, 1, product.getProductName(), normalStyle);
                createCell(row, 2, product.getCategoryName(), normalStyle);
                createCell(row, 3, product.getBrandName(), normalStyle);
                createCell(row, 4, product.getTotalSold(), numberStyle);
                createCell(row, 5, product.getTotalRevenue(), priceStyle);
                createCell(row, 6, product.getAveragePrice(), priceStyle);

                rowIdx++;
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    @Override
    public void exportInventoryToExcel(List<ReportDTO.InventoryReport> inventory,
                                       HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=inventory_report_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo cáo tồn kho");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle normalStyle = createNormalStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            // Header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"STT", "Tên sản phẩm", "Danh mục", "Thương hiệu",
                    "Tồn kho", "Số biến thể", "Trạng thái"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data
            int rowIdx = 1;
            for (ReportDTO.InventoryReport item : inventory) {
                Row row = sheet.createRow(rowIdx);

                createCell(row, 0, rowIdx, numberStyle);
                createCell(row, 1, item.getProductName(), normalStyle);
                createCell(row, 2, item.getCategoryName(), normalStyle);
                createCell(row, 3, item.getBrandName(), normalStyle);
                createCell(row, 4, item.getTotalStock(), numberStyle);
                createCell(row, 5, item.getVariantCount(), numberStyle);

                // Status with color
                Cell statusCell = row.createCell(6);
                statusCell.setCellValue(item.getStockStatusText());
                CellStyle statusStyle = workbook.createCellStyle();
                statusStyle.cloneStyleFrom(normalStyle);

                if ("OUT_OF_STOCK".equals(item.getStockStatus())) {
                    statusStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                    statusStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                } else if ("LOW_STOCK".equals(item.getStockStatus())) {
                    statusStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                    statusStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                } else {
                    statusStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                    statusStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                }
                statusCell.setCellStyle(statusStyle);

                rowIdx++;
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    @Override
    public void exportRevenueToExcel(List<ReportDTO.RevenueReport> revenue,
                                     String groupBy,
                                     HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=revenue_report_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Doanh thu");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle normalStyle = createNormalStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle priceStyle = createPriceStyle(workbook);

            // Header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Kỳ", "Tổng doanh thu", "Số đơn hàng", "Giá trị TB/đơn"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data
            int rowIdx = 1;
            double totalRevenue = 0;
            long totalOrders = 0;

            for (ReportDTO.RevenueReport item : revenue) {
                Row row = sheet.createRow(rowIdx);

                createCell(row, 0, item.getPeriod(), normalStyle);
                createCell(row, 1, item.getTotalRevenue(), priceStyle);
                createCell(row, 2, item.getTotalOrders(), numberStyle);
                createCell(row, 3, item.getAverageOrderValue(), priceStyle);

                totalRevenue += item.getTotalRevenue();
                totalOrders += item.getTotalOrders();

                rowIdx++;
            }

            // Total row
            Row totalRow = sheet.createRow(rowIdx);
            Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("TỔNG CỘNG");
            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);
            totalLabelCell.setCellStyle(boldStyle);

            createCell(totalRow, 1, totalRevenue, priceStyle);
            createCell(totalRow, 2, totalOrders, numberStyle);
            createCell(totalRow, 3, totalOrders > 0 ? totalRevenue / totalOrders : 0, priceStyle);

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    @Override
    public void exportCategoryPerformanceToExcel(List<ReportDTO.CategoryPerformance> performance,
                                                 HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=category_performance_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Hiệu suất danh mục");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle normalStyle = createNormalStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle priceStyle = createPriceStyle(workbook);
            CellStyle percentStyle = createPercentStyle(workbook);

            // Header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Danh mục", "Số sản phẩm", "Đã bán", "Doanh thu", "Thị phần (%)"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data
            int rowIdx = 1;
            for (ReportDTO.CategoryPerformance item : performance) {
                Row row = sheet.createRow(rowIdx);

                createCell(row, 0, item.getCategoryName(), normalStyle);
                createCell(row, 1, item.getProductCount(), numberStyle);
                createCell(row, 2, item.getTotalSold(), numberStyle);
                createCell(row, 3, item.getTotalRevenue(), priceStyle);
                createCell(row, 4, item.getMarketShare() / 100, percentStyle);

                rowIdx++;
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    @Override
    public void exportBrandPerformanceToExcel(List<ReportDTO.BrandPerformance> performance,
                                              HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=brand_performance_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Hiệu suất thương hiệu");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle normalStyle = createNormalStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle priceStyle = createPriceStyle(workbook);
            CellStyle percentStyle = createPercentStyle(workbook);

            // Header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Thương hiệu", "Số sản phẩm", "Đã bán", "Doanh thu", "Thị phần (%)"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data
            int rowIdx = 1;
            for (ReportDTO.BrandPerformance item : performance) {
                Row row = sheet.createRow(rowIdx);

                createCell(row, 0, item.getBrandName(), normalStyle);
                createCell(row, 1, item.getProductCount(), numberStyle);
                createCell(row, 2, item.getTotalSold(), numberStyle);
                createCell(row, 3, item.getTotalRevenue(), priceStyle);
                createCell(row, 4, item.getMarketShare() / 100, percentStyle);

                rowIdx++;
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    @Override
    public void exportComprehensiveReport(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=comprehensive_report_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            // Sheet 1: Dashboard Stats
            createDashboardSheet(workbook);

            // Sheet 2: Best Selling
            createBestSellingSheet(workbook);

            // Sheet 3: Inventory
            createInventorySheet(workbook);

            // Sheet 4: Category Performance
            createCategoryPerformanceSheet(workbook);

            // Sheet 5: Brand Performance
            createBrandPerformanceSheet(workbook);

            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    // ===== HELPER METHODS FOR EXCEL STYLING =====

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createNormalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        return style;
    }

    private CellStyle createPriceStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0 \"₫\""));
        return style;
    }

    private CellStyle createPercentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        return style;
    }

    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }
        cell.setCellStyle(style);
    }

    // ===== COMPREHENSIVE REPORT HELPER METHODS =====

    private void createDashboardSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Tổng quan");
        Map<String, Object> stats = getDashboardStatistics();

        CellStyle labelStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        labelStyle.setFont(boldFont);

        int rowIdx = 0;
        Row titleRow = sheet.createRow(rowIdx++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO TỔNG QUAN");
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);

        rowIdx++; // Empty row

        createStatRow(sheet, rowIdx++, "Tổng số sản phẩm:", stats.get("totalProducts"), labelStyle);
        createStatRow(sheet, rowIdx++, "Tổng người dùng:", stats.get("totalUsers"), labelStyle);
        createStatRow(sheet, rowIdx++, "Tổng đơn hàng:", stats.get("totalOrders"), labelStyle);
        createStatRow(sheet, rowIdx++, "Tổng doanh thu:", stats.get("formattedRevenue"), labelStyle);

        rowIdx++; // Empty row
        createStatRow(sheet, rowIdx++, "Sản phẩm hết hàng:", stats.get("outOfStockProducts"), labelStyle);
        createStatRow(sheet, rowIdx++, "Sản phẩm sắp hết:", stats.get("lowStockProducts"), labelStyle);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createStatRow(Sheet sheet, int rowIdx, String label, Object value, CellStyle labelStyle) {
        Row row = sheet.createRow(rowIdx);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);

        Cell valueCell = row.createCell(1);
        if (value instanceof Long) {
            valueCell.setCellValue((Long) value);
        } else if (value instanceof String) {
            valueCell.setCellValue((String) value);
        }
    }

    private void createBestSellingSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Sản phẩm bán chạy");
        List<ReportDTO.BestSellingProduct> products = getBestSellingProducts(null, null, 20);

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle normalStyle = createNormalStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);
        CellStyle priceStyle = createPriceStyle(workbook);

        Row headerRow = sheet.createRow(0);
        String[] columns = {"STT", "Tên sản phẩm", "Danh mục", "Thương hiệu", "Đã bán", "Doanh thu"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (ReportDTO.BestSellingProduct product : products) {
            Row row = sheet.createRow(rowIdx);
            createCell(row, 0, rowIdx, numberStyle);
            createCell(row, 1, product.getProductName(), normalStyle);
            createCell(row, 2, product.getCategoryName(), normalStyle);
            createCell(row, 3, product.getBrandName(), normalStyle);
            createCell(row, 4, product.getTotalSold(), numberStyle);
            createCell(row, 5, product.getTotalRevenue(), priceStyle);
            rowIdx++;
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createInventorySheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Tồn kho");
        List<ReportDTO.InventoryReport> inventory = getInventoryReport();

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle normalStyle = createNormalStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);

        Row headerRow = sheet.createRow(0);
        String[] columns = {"Sản phẩm", "Danh mục", "Thương hiệu", "Tồn kho", "Trạng thái"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (ReportDTO.InventoryReport item : inventory) {
            Row row = sheet.createRow(rowIdx);
            createCell(row, 0, item.getProductName(), normalStyle);
            createCell(row, 1, item.getCategoryName(), normalStyle);
            createCell(row, 2, item.getBrandName(), normalStyle);
            createCell(row, 3, item.getTotalStock(), numberStyle);
            createCell(row, 4, item.getStockStatusText(), normalStyle);
            rowIdx++;
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createCategoryPerformanceSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Hiệu suất danh mục");
        List<ReportDTO.CategoryPerformance> performance = getCategoryPerformance();

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle normalStyle = createNormalStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);
        CellStyle priceStyle = createPriceStyle(workbook);

        Row headerRow = sheet.createRow(0);
        String[] columns = {"Danh mục", "Số sản phẩm", "Đã bán", "Doanh thu"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (ReportDTO.CategoryPerformance item : performance) {
            Row row = sheet.createRow(rowIdx);
            createCell(row, 0, item.getCategoryName(), normalStyle);
            createCell(row, 1, item.getProductCount(), numberStyle);
            createCell(row, 2, item.getTotalSold(), numberStyle);
            createCell(row, 3, item.getTotalRevenue(), priceStyle);
            rowIdx++;
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createBrandPerformanceSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Hiệu suất thương hiệu");
        List<ReportDTO.BrandPerformance> performance = getBrandPerformance();

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle normalStyle = createNormalStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);
        CellStyle priceStyle = createPriceStyle(workbook);

        Row headerRow = sheet.createRow(0);
        String[] columns = {"Thương hiệu", "Số sản phẩm", "Đã bán", "Doanh thu"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (ReportDTO.BrandPerformance item : performance) {
            Row row = sheet.createRow(rowIdx);
            createCell(row, 0, item.getBrandName(), normalStyle);
            createCell(row, 1, item.getProductCount(), numberStyle);
            createCell(row, 2, item.getTotalSold(), numberStyle);
            createCell(row, 3, item.getTotalRevenue(), priceStyle);
            rowIdx++;
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}