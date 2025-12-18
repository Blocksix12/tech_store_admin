package com.teamforone.tech_store.dto.response;

import com.teamforone.tech_store.model.Coupon.CouponType;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponResponse {
    private String couponId;
    private String code;
    private CouponType type;
    private BigDecimal value;
    private BigDecimal minOrderAmount;
    private Date startsAt;
    private Date endsAt;
    private Integer usageLimit;
    private Integer usedCount;
    private Boolean active;
}
