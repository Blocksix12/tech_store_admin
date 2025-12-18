package com.teamforone.tech_store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
    private String name;
    private String description;
    private Set<PermissionResponse> permissions;

    public String getRoleNameDisplay() {
        if (name == null) {
            return "Chưa xác định";
        }
        switch (name.toUpperCase()) {
            case "ADMIN":
                return "Quản trị viên";
            case "MANAGER":
                return "Quản lý";
            case "STAFF":
                return "Nhân viên";
            default:
                return name;
        }
    }

    @Override
    public String toString() {
        return getRoleNameDisplay();
    }
}
