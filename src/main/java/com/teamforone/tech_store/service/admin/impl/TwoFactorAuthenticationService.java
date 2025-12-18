package com.teamforone.tech_store.service.admin.impl;

import com.teamforone.tech_store.model.VerificationCode;
import com.teamforone.tech_store.service.admin.EmailService;
import com.teamforone.tech_store.service.admin.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TwoFactorAuthenticationService {
    @Autowired
    private SmsService smsService;
    @Autowired
    private EmailService emailService;

    private final Map<String, VerificationCode> emailCodes = new ConcurrentHashMap<>();
    private final Map<String, VerificationCode> phoneCodes = new ConcurrentHashMap<>();


    public String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    // --- SMS 2FA ---
    public void sendVerificationCodePhone(String phoneNumber) {
        String code = generateVerificationCode();
        VerificationCode verificationCode = new VerificationCode(code, LocalDateTime.now().plusMinutes(5));
        phoneCodes.put(phoneNumber, verificationCode);

        String message = "Mã xác thực TechStore: " + code + ". Có hiệu lực 5 phút.";
        smsService.sendSms(phoneNumber, message);
    }

    public boolean verifyPhoneCode(String phoneNumber, String enteredCode) {
        VerificationCode storedCode = phoneCodes.get(phoneNumber);
        if (storedCode == null || storedCode.isUsed()) return false;

        if (storedCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            phoneCodes.remove(phoneNumber);
            return false;
        }

        if (!storedCode.getCode().equals(enteredCode)) return false;

        storedCode.markUsed();
        phoneCodes.remove(phoneNumber);
        return true;
    }


    public void sendVerificationCodeEmail(String email) {
        String code = generateVerificationCode();
        VerificationCode verificationCode = new VerificationCode(code, LocalDateTime.now().plusMinutes(5));
        emailCodes.put(email, verificationCode);

        String content = """
                Xin chào,
                
                Mã xác thực 2FA của bạn là: %s
                
                Mã này chỉ có hiệu lực trong 5 phút và chỉ dùng được 1 lần.
                
                Trân trọng,
                Hệ thống TechStore
                """.formatted(code);

        emailService.sendEmail(email, "Mã xác thực 2FA", content);
    }

    public boolean verifyEmailCode(String email, String enteredCode) {
        VerificationCode storedCode = emailCodes.get(email);
        if (storedCode == null) {
            return false; // Không tồn tại mã
        }

        if (storedCode.isUsed()) {
            return false; // Đã dùng rồi
        }

        if (storedCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            emailCodes.remove(email); // Hết hạn, xoá luôn
            return false;
        }

        if (!storedCode.getCode().equals(enteredCode)) {
            return false;
        }

        // Nếu hợp lệ
        storedCode.markUsed();
        emailCodes.remove(email); // Xoá để không dùng lại
        return true;
    }

}
