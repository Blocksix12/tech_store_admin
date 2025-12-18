package com.teamforone.tech_store.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhanVienResponse {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;

    private Set<RoleResponse> roles;

    public String getRolesName() {
        if (roles == null || roles.isEmpty()) {
            return "STAFF";
        }
        return roles.stream()
                .map(RoleResponse::getName)
                .collect(Collectors.joining(", "));
    }

    public boolean isAdmin() {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.stream()
                .anyMatch(role -> "ADMIN".equalsIgnoreCase(role.getName()));
    }

    public boolean isManager() {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.stream()
                .anyMatch(role -> "MANAGER".equalsIgnoreCase(role.getName()));
    }
}
