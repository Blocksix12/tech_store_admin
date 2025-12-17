package com.teamforone.tech_store.controller.admin.TwoFA;

import com.teamforone.tech_store.service.admin.impl.TwoFactorAuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/2fa/phone")
public class PhoneController {
    private final TwoFactorAuthenticationService twoFactorService;

    public PhoneController(TwoFactorAuthenticationService twoFactorService) {
        this.twoFactorService = twoFactorService;
    }

    // Gửi mã SMS 2FA
    @PostMapping("/send-sms")
    public String sendSms(@RequestParam String phoneNumber) {
        twoFactorService.sendVerificationCodePhone(phoneNumber);
        return "Mã 2FA đã gửi tới " + phoneNumber;
    }

    @PostMapping("/verify-sms")
    public String verifySms(@RequestParam String phoneNumber, @RequestParam String code) {
        boolean valid = twoFactorService.verifyPhoneCode(phoneNumber, code);
        return valid ? "✅ Mã hợp lệ!" : "❌ Mã sai hoặc đã hết hạn!";
    }
}
