package com.teamforone.tech_store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String id;
    private String username;
    private String password;
    private String fullname;
    private String email;
    private String phone;
    private String status;
}
