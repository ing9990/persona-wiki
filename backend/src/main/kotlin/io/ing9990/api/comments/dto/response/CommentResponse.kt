package io.ing9990.api.comments.dto.response

import io.ing9990.domain.figure.Comment

/**
 * 댓글 응답을 위한 DTO
 */
data class CommentResponse(
    val id: Long?,
    val content: String,
    val likes: Int,
    val dislikes: Int,
    val createdAt: String,
) {
    companion object {
        fun from(comment: Comment): CommentResponse {
            return CommentResponse(
                id = comment.id,
                content = comment.content,
                likes = comment.likes,
                dislikes = comment.dislikes,
                createdAt = comment.createdAt.toString(),
            )
        }
    }
}
