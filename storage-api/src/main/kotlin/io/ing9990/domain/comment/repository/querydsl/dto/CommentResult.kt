package io.ing9990.domain.comment.repository.querydsl.dto

import io.ing9990.domain.comment.Comment
import io.ing9990.domain.comment.InteractionType
import io.ing9990.domain.comment.InteractionType.*
import java.time.LocalDateTime

data class CommentResults(
    val comments: List<Comment>,
)

data class CommentResult(
    val id: Long,
    val content: String,
    val likes: Int,
    val dislikes: Int,
    val name: String?,
    val image: String,
    val createdAt: LocalDateTime,
    val userName: String,
    val replyCount: Int, // 답글 개수
    val interactionType: InteractionType?, // 사용자의 상호작용 타입 (좋아요/싫어요)
    val isLikedByUser: Boolean = false, // 사용자가 좋아요를 눌렀는지 여부
) {
    fun hasReplies(): Boolean = replyCount > 0

    companion object {
        fun from(
            it: Comment,
            replyCount: Int = 0,
            userInteractionType: InteractionType? = null,
            isLikedByUser: Boolean = false,
        ): CommentResult =
            CommentResult(
                id = it.id!!,
                content = it.content,
                likes = it.likes,
                dislikes = it.dislikes,
                name = it.user?.nickname,
                image = it.user?.image ?: "",
                createdAt = it.createdAt,
                userName = it.user?.nickname ?: "",
                replyCount = if (replyCount > 0) replyCount else it.repliesCount,
                interactionType = userInteractionType,
                isLikedByUser = isLikedByUser || userInteractionType == LIKE,
            )
    }
}
