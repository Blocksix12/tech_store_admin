package com.teamforone.tech_store.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminReplyRequest {

    private String adminId;
    private String content;
}
