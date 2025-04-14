package io.ing9990.domain.activities.events

import io.ing9990.domain.activities.ActivityEvent
import io.ing9990.domain.activities.ActivityType
import io.ing9990.domain.comment.CommentInteraction
import java.time.LocalDateTime

// 댓글 작성자 처리
data class CommentLikedEvent(
    // 댓글 작성자
    override val userId: Long,
    override val targetId: Long,
    override val targetName: String,
    override val timestamp: LocalDateTime = LocalDateTime.now(),
    override val description: String,
    val commentOverview: String,
    val likeUserId: Long,
    val likedUserId: Long,
) : ActivityEvent(
        userId,
        ActivityType.LIKED,
        targetId,
        targetName,
        commentOverview,
        timestamp,
    ) {
    companion object {
        fun shorten(
            text: String,
            maxLength: Int = 20,
        ): String =
            text.take(maxLength) +
                if (text.length > maxLength) {
                    "..."
                } else {
                    ""
                }

        fun from(interaction: CommentInteraction): CommentLikedEvent {
            val comment = interaction.comment
            val figure = comment.figure

            // 좋아요가 눌러진 댓글 작성자를 위한 이벤트
            return CommentLikedEvent(
                userId =
                    interaction.comment.user?.id
                        ?: throw IllegalArgumentException("User ID is required"),
                targetId =
                    comment.id
                        ?: throw IllegalArgumentException("Comment ID is required"),
                targetName = figure.name,
                commentOverview = shorten(comment.content),
                likeUserId = interaction.user.id!!,
                likedUserId = interaction.comment.user?.id!!,
                description = "다음 사용자가 다음 댓글에 좋아요를 눌렀습니다. ${comment.content}",
            )
        }
    }
}
