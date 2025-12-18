package com.teamforone.tech_store.controller.admin;

import com.teamforone.tech_store.dto.request.AdminReplyRequest;
import com.teamforone.tech_store.dto.response.CommentResponse;
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

    // ====== TRANG QUẢN LÝ ======
    @GetMapping
    public String commentsPage(Model model) {
        List<CommentResponse> comments = commentService.findAllComments();
        model.addAttribute("comments", comments);
        return "admin/Comment";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable String id) {
        commentService.approveComment(id);
        return "redirect:/admin/comments";
    }

    @PostMapping("/{id}/hide")
    public String hide(@PathVariable String id) {
        commentService.hideComment(id);
        return "redirect:/admin/comments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id) {
        commentService.deleteComment(id);
        return "redirect:/admin/comments";
    }

    @PostMapping("/{id}/reply")
    public String reply(@PathVariable String id,
                        @ModelAttribute AdminReplyRequest request) {
        commentService.replyToComment(id, request.getAdminId(), request.getContent());
        return "redirect:/admin/comments";
    }
}
