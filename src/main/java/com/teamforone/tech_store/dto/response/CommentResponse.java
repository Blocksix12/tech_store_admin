package com.teamforone.tech_store.dto.response;

import com.teamforone.tech_store.enums.CommentStatus;
import com.teamforone.tech_store.model.Comment;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private String commentId;

    // USER
    private String userId;
    private String username;

    // PRODUCT
    private String productId;
    private String productName;

    private int rating;
    private String commentText;
    private CommentStatus status;
    private Date createdAt;
    private Integer likeCount;

    private List<ReplyResponse> replies;
}
