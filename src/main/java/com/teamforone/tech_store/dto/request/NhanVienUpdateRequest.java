package com.teamforone.tech_store.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhanVienUpdateRequest {
    private String username;
    private String password;
    private String fullName;
    @Email(message = "Email not a valid format")
    private String email;
    private String phoneNumber;

    private Set<String> roles;
}
