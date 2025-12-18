package com.teamforone.tech_store.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CTProductRequest {
    @NotBlank(message = "Vui lòng chọn sản phẩm")
    private String productId;

    @NotBlank(message = "Vui lòng chọn màu sắc")
    private String colorId;

    private String storageId;  // Optional

    private String sizeId;     // Optional

    @NotNull(message = "Giá gốc không được để trống")
    @DecimalMin(value = "0.01", message = "Giá gốc phải lớn hơn 0")
    private Double price;

    @DecimalMin(value = "0.00", message = "Giá khuyến mãi không được âm")
    private Double salePrice;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    private Integer quantity;

    // Optional: thông tin hiển thị
    private String productName;
    private String colorName;
    private String romName;
    private String sizeName;
    private String imageUrl;

    /**
     * Custom validation: Sale price must be less than price
     */
    @AssertTrue(message = "Giá khuyến mãi phải nhỏ hơn giá gốc")
    public boolean isValidSalePrice() {
        if (salePrice == null || salePrice == 0) {
            return true; // No sale price is valid
        }
        return price == null || salePrice < price;
    }
}
