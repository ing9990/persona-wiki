// backend/src/main/kotlin/io/ing9990/api/comments/dto/response/CommentResponse.kt
package io.ing9990.api.comments.dto.response

import io.ing9990.domain.figure.Comment
import io.ing9990.domain.figure.CommentType

/**
 * 댓글 응답을 위한 DTO
 */
data class CommentResponse(
    val id: Long?,
    val content: String,
    val likes: Int,
    val dislikes: Int,
    val createdAt: String,
    val commentType: CommentType,
    val isReply: Boolean,
    val depth: Int,
    val parentId: Long?,
    val rootId: Long?,
    val replyCount: Int,
    val replies: List<CommentResponse>? = null,
) {
    companion object {
        fun from(
            comment: Comment,
            includeReplies: Boolean = false,
        ): CommentResponse {
            val commentResponse =
                CommentResponse(
                    id = comment.id,
                    content = comment.content,
                    likes = comment.likes,
                    dislikes = comment.dislikes,
                    createdAt = comment.createdAt.toString(),
                    commentType = comment.commentType,
                    isReply = comment.isReply(),
                    depth = comment.depth,
                    parentId = comment.parent?.id,
                    rootId = comment.rootId,
                    replyCount = comment.repliesCount,
                )

            // 답글 목록도 포함할지 결정
            return if (includeReplies && comment.replies.isNotEmpty()) {
                commentResponse.copy(
                    replies = comment.replies.map { from(it, false) },
                )
            } else {
                commentResponse
            }
        }
    }
}
