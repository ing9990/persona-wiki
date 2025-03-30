package io.ing9990.admin.dto

import io.ing9990.domain.figure.Comment
import java.time.LocalDateTime

/**
 * 댓글 정보를 담는 DTO 클래스
 */
data class CommentDto(
    val id: Long?,
    val content: String,
    val likes: Int,
    val dislikes: Int,
    val createdAt: LocalDateTime,
    val figureId: Long,
    val figureName: String,
) {
    companion object {
        fun from(comment: Comment): CommentDto {
            return CommentDto(
                id = comment.id,
                content = comment.content,
                likes = comment.likes,
                dislikes = comment.dislikes,
                createdAt = comment.createdAt,
                figureId = comment.figure.id ?: 0,
                figureName = comment.figure.name,
            )
        }
    }
}
