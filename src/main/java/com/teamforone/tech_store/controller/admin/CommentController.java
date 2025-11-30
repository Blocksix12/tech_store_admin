package com.teamforone.tech_store.controller.admin;

import com.teamforone.tech_store.model.Comment;
import com.teamforone.tech_store.service.admin.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // ====== TRANG QUẢN LÝ COMMENT ======
    @GetMapping
    public String commentsPage(Model model) {
        List<Comment> comments = commentService.findAllComments();
        for (Comment c : comments) {
            System.out.println("CommentID: " + c.getCommentID());
            System.out.println("Status: " + c.getStatus());
            System.out.println("CreatedAt: " + c.getCreatedAt());
        }
        model.addAttribute("comments", comments);
        return "admin/Comment"; // file src/main/resources/templates/admin/comments.html
    }

    // ====== DUYỆT BÌNH LUẬN ======
    @PostMapping("/{id}/approve")
    public String approveComment(@PathVariable("id") String commentId) {
        commentService.approveComment(commentId);
        return "redirect:/admin/comments";
    }

    // ====== ẨN BÌNH LUẬN ======
    @PostMapping("/{id}/hide")
    public String hideComment(@PathVariable("id") String commentId) {
        commentService.hideComment(commentId);
        return "redirect:/admin/comments";
    }

    // ====== XOÁ BÌNH LUẬN ======
    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable("id") String commentId) {
        commentService.deleteComment(commentId);
        return "redirect:/admin/comments";
    }

    // ====== ADMIN TRẢ LỜI BÌNH LUẬN ======
        @PostMapping("/{id}/reply")
        public String replyToComment(
                @PathVariable("id") String commentId,
                @RequestParam("adminId") String adminId,
                @RequestParam("content") String content) {
            commentService.replyToComment(commentId, adminId, content);
            return "redirect:/admin/comments";
        }
}
