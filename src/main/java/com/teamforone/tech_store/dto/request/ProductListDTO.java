package com.teamforone.tech_store.dto.request;

import com.teamforone.tech_store.model.Product;
import lombok.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductListDTO {
    private String id;
    private String name;
    private String slug;
    private String imageUrl;
    private String brands;
    private String sku;
    private String category;
    private String price;
    private Double minPrice;
    private Double maxPrice;
    private Long stockCount;
    private String stockStatus;
    private String stockStatusText;
    private String status;
    private String statusText;

    // Constructor từ Native Query results
    public ProductListDTO(String id, String name, String slug, String categoryName,
                          Double minPrice, Double maxPrice, Long stockCount,
                          Product.Status productStatus, String imageUrl, String brands) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.imageUrl = imageUrl;
        this.category = categoryName != null ? categoryName : "Chưa phân loại";
        this.minPrice = minPrice != null ? minPrice : 0.0;
        this.maxPrice = maxPrice != null ? maxPrice : 0.0;
        this.stockCount = stockCount != null ? stockCount : 0L;

        // Format giá
        this.price = formatPrice(this.minPrice, this.maxPrice);
        this.sku = slug;
        this.brands = brands != null ? brands : "Chưa có thương hiệu";

        // Variant info
//        this.variant = formatVariant(this.stockCount);

        // Set stock status
        setStockStatusFromCount(this.stockCount);

        // Set product status
        setStatusFromEnum(productStatus);
    }

    private String formatPrice(Double min, Double max) {
        if (min == null || max == null || (min == 0 && max == 0)) {
            return "Liên hệ";
        }

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        if (min.equals(max)) {
            return formatter.format(min) + " ₫";
        }

        return formatter.format(min) + "₫" + " -> " + formatter.format(max) + "₫";
    }

    private String formatVariant(Long count) {
        if (count == null || count == 0) {
            return "Chưa có biến thể";
        }
        return count + " biến thể";
    }

    private void setStockStatusFromCount(Long count) {
        if (count == null || count == 0) {
            this.stockStatus = "OUT_OF_STOCK";
            this.stockStatusText = "Hết hàng";
        } else if (count < 10) {
            this.stockStatus = "LOW_STOCK";
            this.stockStatusText = "Sắp hết";
        } else {
            this.stockStatus = "IN_STOCK";
            this.stockStatusText = "Còn hàng";
        }
    }

    private void setStatusFromEnum(Product.Status productStatus) {
        if (productStatus == null) {
            this.status = "DRAFT";
            this.statusText = "Nháp";
            return;
        }

        switch (productStatus) {
            case PUBLISHED:
                this.status = "ACTIVE";
                this.statusText = "Đang bán";
                break;
            case ARCHIVED:
                this.status = "INACTIVE";
                this.statusText = "Ngừng bán";
                break;
            case DRAFT:
            default:
                this.status = "DRAFT";
                this.statusText = "Nháp";
                break;
        }
    }
}
