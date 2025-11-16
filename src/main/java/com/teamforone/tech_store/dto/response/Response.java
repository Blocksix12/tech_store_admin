package com.teamforone.tech_store.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor  // ← Thêm dòng này
@NoArgsConstructor
public class Response {
    private int status;
    private String message;
}
