package com.teamforone.tech_store.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInventorySummary {
    private String productId;
    private String productName;
    private Integer totalQuantity;        // Tổng số lượng tất cả variants
    private Integer totalVariants;        // Số lượng variants
    private Double totalValue;            // Tổng giá trị
    private String overallStatus;         // IN_STOCK, LOW_STOCK, OUT_OF_STOCK
    private List<InventoryResponse> variants;
}