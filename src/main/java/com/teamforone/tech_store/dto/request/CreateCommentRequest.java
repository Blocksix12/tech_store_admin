package com.teamforone.tech_store.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequest {
    private String productId;
    private String userId;
    private int rating;
    private String commentText;
}
