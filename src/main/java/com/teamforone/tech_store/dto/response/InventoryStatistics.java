package com.teamforone.tech_store.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryStatistics {
    private Long totalVariants;           // Tổng số variants
    private Long totalProducts;           // Tổng số products
    private Integer totalQuantity;        // Tổng số lượng tồn kho
    private Double totalValue;            // Tổng giá trị tồn kho
    private Long outOfStockCount;         // Số variant hết hàng
    private Long lowStockCount;           // Số variant sắp hết
    private Long inStockCount;            // Số variant còn hàng
}