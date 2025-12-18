package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.CreateCouponRequest;
import com.teamforone.tech_store.dto.response.CouponResponse;
import com.teamforone.tech_store.model.Coupon;
import com.teamforone.tech_store.repository.admin.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    // ====== CREATE ======
    @Transactional
    public CouponResponse createCoupon(CreateCouponRequest request) {
        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .type(request.getType())
                .value(request.getValue())
                .minOrderAmount(request.getMinOrderAmount())
                .startsAt(request.getStartsAt())
                .endsAt(request.getEndsAt())
                .usageLimit(request.getUsageLimit())
                .active(true)
                .usedCount(0)
                .build();

        Coupon saved = couponRepository.save(coupon);
        return toResponse(saved);
    }

    // ====== GET ALL ======
    public List<CouponResponse> getAllCoupons() {
        LocalDate today = LocalDate.now();

        return couponRepository.findAll().stream()
                .peek(c -> {
                    LocalDate endsAtDate = c.getEndsAt().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    // Nếu đã hết hạn, tự động set inactive
                    if (endsAtDate.isBefore(today)) {
                        c.setActive(false);
                    }
                })
                .map(c -> CouponResponse.builder()
                        .couponId(c.getCouponId())
                        .code(c.getCode())
                        .type(c.getType())
                        .value(c.getValue())
                        .minOrderAmount(c.getMinOrderAmount())
                        .startsAt(c.getStartsAt())
                        .endsAt(c.getEndsAt())
                        .usageLimit(c.getUsageLimit())
                        .usedCount(c.getUsedCount())  // đúng tên field
                        .active(c.getActive())
                        .build())
                .collect(Collectors.toList());
    }


    // ====== GET BY ID ======
    public CouponResponse getCoupon(String id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        return toResponse(coupon);
    }

    // ====== UPDATE ======
    @Transactional
    public CouponResponse updateCoupon(String id, CreateCouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        coupon.setCode(request.getCode());
        coupon.setType(request.getType());
        coupon.setValue(request.getValue());
        coupon.setMinOrderAmount(request.getMinOrderAmount());
        coupon.setStartsAt(request.getStartsAt());
        coupon.setEndsAt(request.getEndsAt());
        coupon.setUsageLimit(request.getUsageLimit());

        return toResponse(couponRepository.save(coupon));
    }

    // ====== DELETE ======
    @Transactional
    public void deleteCoupon(String id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        couponRepository.delete(coupon);
    }

    // ====== HELPER ======
    private CouponResponse toResponse(Coupon c) {
        return CouponResponse.builder()
                .couponId(c.getCouponId())
                .code(c.getCode())
                .type(c.getType())
                .value(c.getValue())
                .minOrderAmount(c.getMinOrderAmount())
                .startsAt(c.getStartsAt())
                .endsAt(c.getEndsAt())
                .usageLimit(c.getUsageLimit())
                .usedCount(c.getUsedCount())
                .active(c.getActive())
                .build();
    }

    @Transactional
    public void toggleActive(String id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        coupon.setActive(!coupon.getActive());
        couponRepository.save(coupon);
    }
}
