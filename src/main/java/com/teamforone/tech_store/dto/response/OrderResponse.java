package com.teamforone.tech_store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO để trả về thông tin đơn hàng cho client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    // Thông tin đơn hàng
    private String orderId;              // ID đơn hàng
    private String orderNo;              // Mã đơn hàng (ORD20241113...)
    private BigDecimal totalAmount;      // Tổng tiền
    private String paymentMethod;        // Phương thức thanh toán
    private LocalDateTime createdAt;     // Ngày tạo đơn

    // Thông tin khách hàng
    private String userId;
    private String userName;
    private String userEmail;
    private String userPhone;

    // Thông tin giao hàng
    private String shippingId;
    private BigDecimal shippingPrice;    // Phí ship

    // Thông tin nhân viên xử lý
    private String nhanvienId;
    private String nhanvienName;

    // Danh sách sản phẩm trong đơn
    private List<OrderItemResponse> items;

    /**
     * DTO cho từng sản phẩm trong đơn hàng
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private String orderItemId;      // ID của item
        private String productId;
        private String productName;      // Tên sản phẩm
        private String colorName;        // Tên màu
        private String displaySize;      // Kích thước màn hình
        private String storage;          // RAM/ROM
        private Integer quantity;        // Số lượng
        private BigDecimal price;        // Giá 1 sản phẩm
        private BigDecimal subtotal;     // Tổng tiền = price * quantity
        private String orderStatus;      // Trạng thái: PENDING, PAID, PROCESSING...
    }
}