// backend/src/main/kotlin/io/ing9990/api/comments/dto/response/CommentResponse.kt
package io.ing9990.api.comments.dto.response

import io.ing9990.domain.comment.Comment
import io.ing9990.domain.comment.CommentType
import io.ing9990.domain.comment.InteractionType

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
    val userId: Long?,
    val userNickname: String?,
    val userProfileImage: String?,
    val userInteraction: InteractionType? = null,
) {
    // companion object의 from 메서드도 수정:
    companion object {
        fun from(
            comment: Comment,
            userId: Long? = null,
            includeReplies: Boolean = false,
        ): CommentResponse {
            val userInteraction =
                if (userId != null) {
                    comment.getUserInteraction(userId)?.interactionType
                } else {
                    null
                }

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
                    userId = comment.user?.id,
                    userNickname = comment.user?.nickname,
                    userProfileImage = comment.user?.image,
                    userInteraction = userInteraction,
                )

            // 답글 목록도 포함할지 결정
            return if (includeReplies && comment.replies.isNotEmpty()) {
                commentResponse.copy(
                    replies = comment.replies.map { from(it, userId, false) },
                )
            } else {
                commentResponse
            }
        }
    }
}
