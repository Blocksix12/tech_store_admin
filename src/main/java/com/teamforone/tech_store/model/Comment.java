package com.teamforone.tech_store.model;

import com.teamforone.tech_store.enums.CommentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
public class Comment {

    @Id
    @UuidGenerator
    @Column(name = "comment_id", updatable = false, nullable = false)
    private String commentID;

    @Column(name = "product_id", nullable = false, columnDefinition = "CHAR(36)")
    private String product;

    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private String user;

    @Column(name = "rating", columnDefinition = "INT CHECK (rating >= 1 AND rating <= 5)")
    private int rating;

    @Column(name = "noidung", columnDefinition = "TEXT", nullable = false)
    private String commentText;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CommentStatus status;

    @CreationTimestamp
    @Column(name = "ngaybl", nullable = false, insertable = false, updatable = false)
    private Date createdAt;

    @Column(name = "luotthich", columnDefinition = "INT DEFAULT 0")
    private Integer likeCount;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Reply> replies;

    // Tự động set ID và default status trước khi persist
    @PrePersist
    public void prePersist() {
        if (this.commentID == null) {
            this.commentID = UUID.randomUUID().toString();
        }
        if (this.status == null) {
            this.status = CommentStatus.PENDING;
        }
    }
}
