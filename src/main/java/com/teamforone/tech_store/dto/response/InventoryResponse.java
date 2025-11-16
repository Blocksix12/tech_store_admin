package com.teamforone.tech_store.dto.response;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryResponse {
    private String productId;
    private String productName;
    private String colorId;
    private String colorName;
    private String storageId;
    private String storageName;
    private String sizeId;
    private String sizeName;
    private Integer quantity;
    private Double price;
    private Double salePrice;
    private String stockStatus; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK
}