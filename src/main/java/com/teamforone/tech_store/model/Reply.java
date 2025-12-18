package com.teamforone.tech_store.model;

import com.teamforone.tech_store.enums.CommentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "reply")
public class Reply {
    @Id
    @UuidGenerator
    @Column(name = "reply_id", columnDefinition = "CHAR(36)")
    private String replyID;


    @Column(name = "comment_id", nullable = false)
    private String comment;

    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private String user;

    @Column(name = "noidung", columnDefinition = "TEXT", nullable = false)
    private String commentText;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'pending'")
    private CommentStatus status;

    @CreationTimestamp
    @Column(name = "ngaytra", nullable = false)
    private Date createdAt;

    @Column(name = "luotthich", columnDefinition = "INT DEFAULT 0")
    private Integer likeCount;

}
