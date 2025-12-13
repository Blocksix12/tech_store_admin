package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.dto.request.CreateCommentRequest;
import com.teamforone.tech_store.dto.response.CommentResponse;
import com.teamforone.tech_store.dto.response.ReplyResponse;
import com.teamforone.tech_store.enums.CommentStatus;
import com.teamforone.tech_store.model.Comment;
import com.teamforone.tech_store.model.Reply;
import com.teamforone.tech_store.model.Product;
import com.teamforone.tech_store.model.User;
import com.teamforone.tech_store.repository.admin.CommentRepository;
import com.teamforone.tech_store.repository.admin.ReplyRepository;
import com.teamforone.tech_store.repository.admin.crud.ProductRepository;
import com.teamforone.tech_store.repository.admin.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CommentService(CommentRepository commentRepository,
                          ReplyRepository replyRepository,
                          UserRepository userRepository,
                          ProductRepository productRepository) {
        this.commentRepository = commentRepository;
        this.replyRepository = replyRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // ====== TẠO COMMENT ======
    @Transactional
    public void createComment(CreateCommentRequest request) {
        Comment comment = Comment.builder()
                .product(request.getProductId()) // lưu ID kiểu String
                .user(request.getUserId())       // lưu ID kiểu String
                .rating(request.getRating())
                .commentText(request.getCommentText())
                .status(CommentStatus.PENDING)
                .build();

        commentRepository.save(comment);
    }

    // ====== LẤY COMMENT ======
    public List<CommentResponse> findAllComments() {
        return commentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ====== DUYỆT ======
    @Transactional
    public void approveComment(String commentId) {
        Comment comment = getComment(commentId);
        comment.setStatus(CommentStatus.APPROVED);
    }

    // ====== ẨN ======
    @Transactional
    public void hideComment(String commentId) {
        Comment comment = getComment(commentId);
        comment.setStatus(CommentStatus.REJECTED);
        comment.setCommentText("[Nội dung đã bị ẩn do vi phạm chính sách]");
    }

    // ====== XOÁ ======
    @Transactional
    public void deleteComment(String commentId) {
        Comment comment = getComment(commentId);
        replyRepository.deleteAllByCommentId(comment.getCommentID());
        commentRepository.delete(comment);
    }

    // ====== ADMIN TRẢ LỜI ======
    @Transactional
    public void replyToComment(String commentId, String adminId, String content) {
        getComment(commentId);

        Reply reply = Reply.builder()
                .comment(commentId)
                .user(adminId)
                .commentText(content)
                .status(CommentStatus.APPROVED)
                .likeCount(0)
                .build();

        replyRepository.save(reply);
    }

    // ====== HELPER ======
    private Comment getComment(String id) {
        if (id == null) throw new RuntimeException("Comment ID is null");
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    private CommentResponse toResponse(Comment c) {
        // Lấy user và product từ DB, kiểm tra null trước khi findById
        User user = null;
        if (c.getUser() != null) {
            user = userRepository.findById(c.getUser())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        Product product = null;
        if (c.getProduct() != null) {
            product = productRepository.findById(c.getProduct())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
        }

        return CommentResponse.builder()
                .commentId(c.getCommentID())
                .userId(user != null ? user.getId() : null)
                .username(user != null ? user.getUsername() : "[Unknown User]")
                .productId(product != null ? product.getId() : null)
                .productName(product != null ? product.getName() : "[Unknown Product]")
                .rating(c.getRating())
                .commentText(c.getCommentText())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                .likeCount(c.getLikeCount())
                .replies(
                        c.getReplies() != null
                                ? c.getReplies().stream()
                                .map(this::toReplyResponse)
                                .toList()
                                : null
                )
                .build();
    }

    private ReplyResponse toReplyResponse(Reply r) {
        User user = null;
        if (r.getUser() != null) {
            user = userRepository.findById(r.getUser())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        return ReplyResponse.builder()
                .replyId(r.getReplyID())
                .commentId(r.getComment())
                .userId(user != null ? user.getId() : null)
                .username(user != null ? user.getUsername() : "[Unknown User]")
                .commentText(r.getCommentText())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .likeCount(r.getLikeCount())
                .build();
    }
}
