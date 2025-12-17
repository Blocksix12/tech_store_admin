package com.teamforone.tech_store.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message = "Username is required, must not be blank")
    private String username;
    @NotBlank(message = "Password is required, must not be blank")
    private String password;
    private String fullName;
    @Email(message = "Email not a valid format")
    @NotBlank(message = "Email is required, must not be blank")
    private String email;
    @NotBlank(message = "Phone number is required, must not be blank")
    private String phoneNumber;
}
