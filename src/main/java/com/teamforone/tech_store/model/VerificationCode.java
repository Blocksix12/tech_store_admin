package com.teamforone.tech_store.model;

import java.time.LocalDateTime;

public class VerificationCode {
    private String code;
    private LocalDateTime expiryTime;
    private boolean used;

    public VerificationCode(String code, LocalDateTime expiryTime) {
        this.code = code;
        this.expiryTime = expiryTime;
        this.used = false;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public boolean isUsed() {
        return used;
    }

    public void markUsed() {
        this.used = true;
    }
}
