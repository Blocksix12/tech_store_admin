package com.teamforone.tech_store.enums;

import lombok.Getter;

@Getter
public enum BrandStatus {
    ACTIVE("Đang hoạt động"),
    INACTIVE("Ngừng hoạt động"),
    PENDING("Chờ duyệt"),
    DISCONTINUED("Ngừng kinh doanh");

    private final String displayName;

    BrandStatus(String displayName) {
        this. displayName = displayName;
    }

    public String getBadgeClass() {
        switch (this) {
            case ACTIVE:
                return "bg-success";
            case INACTIVE:
                return "bg-secondary";
            case PENDING:
                return "bg-warning text-dark";
            case DISCONTINUED:
                return "bg-danger";
            default:
                return "bg-secondary";
        }
    }
}