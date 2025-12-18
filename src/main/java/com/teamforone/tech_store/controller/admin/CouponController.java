package com.teamforone.tech_store.controller.admin;

import com.teamforone.tech_store.dto.request.CreateCouponRequest;
import com.teamforone.tech_store.dto.response.CouponResponse;
import com.teamforone.tech_store.service.admin.CouponService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    // ====== LIST ALL ======
    @GetMapping
    public String listCoupons(Model model) {
        List<CouponResponse> coupons = couponService.getAllCoupons();
        model.addAttribute("coupons", coupons);
        return "admin/coupon-list";
    }

    // ====== CREATE FORM ======
    @GetMapping("/create")
    public String createCouponForm(Model model) {
        model.addAttribute("coupon", new CreateCouponRequest());
        return "admin/coupon-form";
    }

    @PostMapping("/create")
    public String createCoupon(@ModelAttribute CreateCouponRequest request) {
        couponService.createCoupon(request);
        return "redirect:/admin/coupons";
    }

    // ====== EDIT FORM ======
    @GetMapping("/edit/{id}")
    public String editCouponForm(@PathVariable String id, Model model) {
        CouponResponse couponResponse = couponService.getCoupon(id);

        // Chuyển CouponResponse sang CreateCouponRequest để bind form dễ dàng
        CreateCouponRequest couponRequest = new CreateCouponRequest();
        couponRequest.setCode(couponResponse.getCode());
        couponRequest.setType(couponResponse.getType());
        couponRequest.setValue(couponResponse.getValue());
        couponRequest.setMinOrderAmount(couponResponse.getMinOrderAmount());
        couponRequest.setStartsAt(couponResponse.getStartsAt());
        couponRequest.setEndsAt(couponResponse.getEndsAt());
        couponRequest.setUsageLimit(couponResponse.getUsageLimit());
        couponRequest.setActive(couponResponse.getActive());

        model.addAttribute("coupon", couponRequest);
        model.addAttribute("couponId", id); // truyền id riêng để dùng trong form action
        return "admin/coupon-form";
    }

    @PostMapping("/edit/{id}")
    public String editCoupon(@PathVariable String id, @ModelAttribute CreateCouponRequest request) {
        couponService.updateCoupon(id, request);
        return "redirect:/admin/coupons";
    }

    // ====== DELETE ======
    @PostMapping("/delete/{id}")
    public String deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
        return "redirect:/admin/coupons";
    }

    // ===== Toggle Active =====
    @PostMapping("/toggle-active/{id}")
    public String toggleCouponActive(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        couponService.toggleActive(id);
        return "redirect:/admin/coupons";
    }

}
