package com.teamforone.tech_store.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Builder
@Setter
public class LoginResponse {
    private String userId;
    @JsonProperty("access_token")
    private String accessToken;
    private int status;
    private String message;

    @JsonProperty("refresh_token")
    private String refreshToken;

    private String username;

    @JsonProperty("user_type")
    private String userType; // "NHANVIEN" hoặc "USER"

    @JsonProperty("token_type")
    @Builder.Default
    private String tokenType = "Bearer";
    private Set<String> roles;
}
