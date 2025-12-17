package com.teamforone.tech_store.dto.request;

import lombok.*;

import java.time.LocalDate;

public class ReportDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BestSellingProduct {
        private String productId;
        private String productName;
        private String productImage;
        private String categoryName;
        private String brandName;
        private Long totalSold;
        private Double totalRevenue;
        private String formattedRevenue;
        private Double averagePrice;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InventoryReport {
        private String productId;
        private String productName;
        private String productImage;
        private String categoryName;
        private String brandName;
        private Long totalStock;
        private Long variantCount;
        private String stockStatus; // "OUT_OF_STOCK", "LOW_STOCK", "IN_STOCK"
        private String stockStatusText;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RevenueReport {
        private String period; // e.g., "2024-01", "2024-W01", "2024-01-01"
        private Double totalRevenue;
        private String formattedRevenue;
        private Long totalOrders;
        private Double averageOrderValue;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CategoryPerformance {
        private String categoryId;
        private String categoryName;
        private Long productCount;
        private Long totalSold;
        private Double totalRevenue;
        private String formattedRevenue;
        private Double marketShare; // % of total revenue
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BrandPerformance {
        private String brandId;
        private String brandName;
        private String logoUrl;
        private Long productCount;
        private Long totalSold;
        private Double totalRevenue;
        private String formattedRevenue;
        private Double marketShare; // % of total revenue
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DashboardStats {
        private Long totalProducts;
        private Long totalUsers;
        private Long totalOrders;
        private Double totalRevenue;
        private String formattedRevenue;
        private Long lowStockProducts;
        private Long outOfStockProducts;
        private Long activeUsers;
        private Long lockedUsers;
        private Long todayOrders;
        private Double todayRevenue;
        private String formattedTodayRevenue;
    }
}