package com.teamforone.tech_store.repository.admin;

import com.teamforone.tech_store.model.Comment;
import com.teamforone.tech_store.model.Reply;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, String> {
    @Query("SELECT r FROM Reply r WHERE r.comment = :commentId ORDER BY r.createdAt ASC")
    List<Reply> findByCommentIdOrderByCreatedAtAsc(@Param("commentId") String commentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Reply r WHERE r.comment = :commentId")
    void deleteAllByCommentId(@Param("commentId") String commentId);
}
