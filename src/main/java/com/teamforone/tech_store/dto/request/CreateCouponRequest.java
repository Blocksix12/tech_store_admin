package com.teamforone.tech_store.dto.request;

import com.teamforone.tech_store.model.Coupon.CouponType;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCouponRequest {
    private String couponId;
    private String code;                  // Mã giảm giá
    private CouponType type;              // Loại: PERCENT, FIXED, FREE_SHIPPING
    private BigDecimal value;             // Giá trị giảm
    private BigDecimal minOrderAmount;    // Giá trị tối thiểu đơn hàng
    private Date startsAt;                // Ngày bắt đầu
    private Date endsAt;                  // Ngày kết thúc
    private Integer usageLimit;           // Giới hạn số lần sử dụng
    private Boolean active = true;        // Kích hoạt hay không
}
