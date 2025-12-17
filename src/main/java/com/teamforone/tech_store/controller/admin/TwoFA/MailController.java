package com.teamforone.tech_store.controller.admin.TwoFA;

import com.teamforone.tech_store.service.admin.EmailService;
import com.teamforone.tech_store.service.admin.impl.TwoFactorAuthenticationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/2fa/mail")
public class MailController {
    private final TwoFactorAuthenticationService twoFactorService;

    public MailController(TwoFactorAuthenticationService twoFactorService) {
        this.twoFactorService = twoFactorService;
    }

    // Gửi mã xác thực qua email
    @GetMapping("/email/send")
    public String sendEmailCode(@RequestParam String email) {
        twoFactorService.sendVerificationCodeEmail(email);
        return "✅ Mã xác thực đã được gửi tới email: " + email;
    }

    // Xác minh mã xác thực email
    @PostMapping("/email/verify")
    public String verifyEmailCode(@RequestParam String email, @RequestParam String code) {
        boolean isValid = twoFactorService.verifyEmailCode(email, code);
        return isValid ? "✅ Mã hợp lệ, đăng nhập thành công!" : "❌ Mã không hợp lệ hoặc đã hết hạn.";
    }
}
