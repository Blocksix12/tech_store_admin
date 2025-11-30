package com.teamforone.tech_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reply")
public class Reply {
    @Id
    @UuidGenerator
    @Column(name = "reply_id", columnDefinition = "CHAR(36)")
    private String replyID;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "noidung", columnDefinition = "TEXT", nullable = false)
    private String commentText;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'pending'")
    private Comment.Status status;

    @CreationTimestamp
    @Column(name = "ngaytra", nullable = false)
    private Date createdAt;

    @Column(name = "luotthich", columnDefinition = "INT DEFAULT 0")
    private Integer likeCount;


    public enum Status {
        PENDING,
        APPROVED,
        REJECTED;

        private static Comment.Status toEnum(String value) {
            for(Comment.Status status : Comment.Status.values()){
                if (status.toString().equalsIgnoreCase(value)) return status;
            }
            return null;
        }
    }
}
