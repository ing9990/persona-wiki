package io.ing9990.api.comments.dto.response

import io.ing9990.domain.comment.InteractionType
import io.ing9990.domain.comment.repository.querydsl.dto.CommentResult
import java.time.format.DateTimeFormatter

/**
 * 댓글 응답을 위한 DTO
 */
data class CommentResponse(
    val id: Long,
    val content: String,
    val likes: Int,
    val dislikes: Int,
    val createdAt: String,
    val replyCount: Int = 0,
    val userId: Long? = null,
    val userNickname: String? = null,
    val userProfileImage: String? = null,
    val userInteraction: InteractionType? = null,
    val isLikedByUser: Boolean = false,
    val isDislikedByUser: Boolean = false,
) {
    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        fun from(result: CommentResult): CommentResponse {
            // 사용자 상호작용 정보
            val userInteraction = result.interactionType
            val isLikedByUser = userInteraction == InteractionType.LIKE
            val isDislikedByUser = userInteraction == InteractionType.DISLIKE

            return CommentResponse(
                id = result.id,
                content = result.content,
                likes = result.likes,
                dislikes = result.dislikes,
                createdAt = result.createdAt.format(DATE_FORMATTER),
                replyCount = result.replyCount,
                userNickname = result.userName,
                userProfileImage = result.image,
                userId = result.userId,
                userInteraction = userInteraction,
                isLikedByUser = isLikedByUser,
                isDislikedByUser = isDislikedByUser,
            )
        }
    }

    fun toWriterProfile(): String = "/users/$userNickname"
}
