package io.ing9990.domain.comment.repository.querydsl.dto

import io.ing9990.domain.comment.Comment
import io.ing9990.domain.comment.InteractionType
import java.time.LocalDateTime

data class CommentResult(
    val id: Long,
    val content: String,
    val likes: Int,
    val dislikes: Int,
    val name: String?,
    val image: String,
    val createdAt: LocalDateTime,
    val userName: String,
    val userId: Long,
    val replyCount: Int,
    val interactionType: InteractionType?,
    val isLikedByUser: Boolean = false,
) {
    companion object {
        fun from(
            comment: Comment,
            replyCount: Int = 0,
            userInteractionType: InteractionType? = null,
            isLikedByUser: Boolean = false,
        ): CommentResult =
            CommentResult(
                id = comment.id!!,
                content = comment.content,
                likes = comment.likes,
                dislikes = comment.dislikes,
                name = comment.user?.nickname,
                image = comment.user?.image ?: "",
                createdAt = comment.createdAt,
                userName = comment.user?.nickname ?: "",
                userId = comment.user?.id ?: -1,
                replyCount = if (replyCount > 0) replyCount else comment.repliesCount,
                interactionType = userInteractionType,
                isLikedByUser = isLikedByUser || userInteractionType == InteractionType.LIKE,
            )
    }
}
