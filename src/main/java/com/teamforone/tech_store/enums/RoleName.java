package com.teamforone.tech_store.enums;

public enum RoleName {
    ROLE_ADMIN("Quản trị viên", "Quyền toàn quyền hệ thống"),
    ROLE_MANAGER("Quản lý", "Quản lý nhân viên và kho hàng"),
    ROLE_STAFF("Nhân viên", "Nhân viên bán hàng"),
    ROLE_USER("Người dùng", "Khách hàng thông thường");

    private final String displayName;
    private final String description;

    RoleName(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    // Helper method để lấy tên role có prefix ROLE_
    public String getAuthority() {
        return this.name();
    }

    // Helper method để check role
    public boolean isAdmin() {
        return this == ROLE_ADMIN;
    }

    public boolean isManager() {
        return this == ROLE_MANAGER;
    }

    public boolean isStaff() {
        return this == ROLE_STAFF;
    }

    public static RoleName toEnum(String value) {
        for (RoleName item : values()) {
            if (item.toString().equalsIgnoreCase(value)) return item;
        }
        return null;
    }
}
