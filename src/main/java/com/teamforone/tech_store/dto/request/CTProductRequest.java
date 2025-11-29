package com.teamforone.tech_store.dto.request;

import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CTProductRequest {
    private String productId;
    private String colorId;
    private String storageId;
    private String sizeId;

    private Double price;
    private Double salePrice;
    private Integer quantity;

    // Optional: thông tin hiển thị
    private String productName;
    private String colorName;
    private String ramName;
    private String romName;
    private String sizeName;

    private String imageUrl;
}
