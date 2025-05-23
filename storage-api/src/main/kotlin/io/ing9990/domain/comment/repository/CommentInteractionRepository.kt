package io.ing9990.domain.comment.repository

import io.ing9990.domain.comment.CommentInteraction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CommentInteractionRepository : JpaRepository<CommentInteraction, Long> {
    @Query("SELECT ci FROM CommentInteraction ci WHERE ci.user.id = ?1 AND ci.comment.id = ?2")
    fun findByUserIdAndCommentId(
        userId: Long,
        commentId: Long,
    ): CommentInteraction?

    @Query("SELECT COUNT(ci) > 0 FROM CommentInteraction ci WHERE ci.user.id = ?1 AND ci.comment.id = ?2")
    fun existsByUserIdAndCommentId(
        userId: Long,
        commentId: Long,
    ): Boolean
}
