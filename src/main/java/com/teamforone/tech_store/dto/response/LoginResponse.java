package com.teamforone.tech_store.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class LoginResponse {
    private int status;
    private String message;

    private String userId;
    private String accessToken;
    private String refreshToken;
}
