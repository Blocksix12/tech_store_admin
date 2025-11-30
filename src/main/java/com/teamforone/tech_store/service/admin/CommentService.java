package com.teamforone.tech_store.service.admin;

import com.teamforone.tech_store.model.Comment;
import com.teamforone.tech_store.model.Reply;
import com.teamforone.tech_store.model.User;
import com.teamforone.tech_store.repository.admin.CommentRepository;
import com.teamforone.tech_store.repository.admin.ReplyRepository;
import com.teamforone.tech_store.repository.admin.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          ReplyRepository replyRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.replyRepository = replyRepository;
        this.userRepository = userRepository;
    }

    // ====== LẤY DANH SÁCH BÌNH LUẬN ======
    public List<Comment> findAllComments() {
        return commentRepository.findAllByOrderByCreatedAtDesc();
    }

    // ====== DUYỆT BÌNH LUẬN ======
    @Transactional
    public Comment approveComment(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setStatus(Comment.Status.APPROVED);
        return commentRepository.save(comment);
    }

    // ====== ẨN NỘI DUNG XẤU ======
    @Transactional
    public Comment hideComment(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setStatus(Comment.Status.REJECTED);
        comment.setCommentText("[Nội dung đã bị ẩn do vi phạm chính sách]");

        return commentRepository.save(comment);
    }

    // ====== XOÁ BÌNH LUẬN ======
    @Transactional
    public void deleteComment(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Xóa luôn các phản hồi liên quan
        replyRepository.deleteAllByComment(comment);
        commentRepository.delete(comment);
    }

    // ====== ADMIN TRẢ LỜI BÌNH LUẬN ======
    @Transactional
    public Reply replyToComment(String commentId, String adminId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Reply reply = new Reply();
        reply.setComment(comment);
        reply.setUser(admin);
        reply.setCommentText(content);
        reply.setStatus(Comment.Status.APPROVED); // admin trả lời thì auto duyệt
        reply.setLikeCount(0);

        return replyRepository.save(reply);
    }
}
