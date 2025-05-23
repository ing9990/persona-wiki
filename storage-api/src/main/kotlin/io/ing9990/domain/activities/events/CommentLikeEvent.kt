package io.ing9990.domain.activities.events

import io.ing9990.domain.activities.ActivityEvent
import io.ing9990.domain.activities.ActivityType
import io.ing9990.domain.comment.CommentInteraction
import io.ing9990.domain.comment.InteractionType
import java.time.LocalDateTime

data class CommentLikeEvent(
    override val userId: Long,
    override val targetId: Long,
    override val targetName: String,
    override val activityType: ActivityType,
    override val categoryId: String,
    override val commentId: Long?,
    val figureId: Long,
    val interactionType: InteractionType,
    override val timestamp: LocalDateTime = LocalDateTime.now(),
    val commentOverview: String,
) : ActivityEvent(
        userId,
        ActivityType.LIKE,
        targetId,
        targetName,
        commentOverview,
        timestamp,
        categoryId = categoryId,
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

        fun from(interaction: CommentInteraction): CommentLikeEvent {
            val comment = interaction.comment
            val figure = comment.figure

            // 좋아요를 누른 사람을 위한 이벤트
            return CommentLikeEvent(
                userId =
                    interaction.user.id
                        ?: throw IllegalArgumentException("User ID is required"),
                targetId =
                    comment.id
                        ?: throw IllegalArgumentException("Comment ID is required"),
                targetName = figure.name,
                figureId =
                    figure.id
                        ?: throw IllegalArgumentException("Figure ID is required"),
                interactionType = interaction.interactionType,
                commentOverview = shorten(comment.content),
                commentId = comment.rootId!!,
                activityType = ActivityType.LIKE,
                categoryId = comment.figure.category.id,
            )
        }
    }
}
