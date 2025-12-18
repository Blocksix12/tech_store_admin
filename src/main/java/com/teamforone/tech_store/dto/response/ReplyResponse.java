package com.teamforone.tech_store.dto.response;

import com.teamforone.tech_store.enums.CommentStatus;
import com.teamforone.tech_store.model.Comment;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyResponse {

    private String replyId;
    private String commentId;
    private String userId;
    private String username;
    private String commentText;
    private CommentStatus status;
    private Date createdAt;
    private Integer likeCount;
}
