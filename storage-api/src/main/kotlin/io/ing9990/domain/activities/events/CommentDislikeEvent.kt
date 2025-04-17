package io.ing9990.domain.activities.events

import io.ing9990.domain.activities.ActivityEvent
import io.ing9990.domain.activities.ActivityType
import io.ing9990.domain.activities.ActivityType.DISLIKE
import io.ing9990.domain.comment.CommentInteraction
import io.ing9990.domain.comment.InteractionType
import java.time.LocalDateTime

data class CommentDislikeEvent(
    override val userId: Long,
    override val targetId: Long,
    override val targetName: String,
    override val description: String? = null,
    override val categoryId: String,
    override val activityType: ActivityType,
    override val commentId: Long?,
    val figureId: Long,
    val interactionType: InteractionType,
    val commentOverview: String,
    override val timestamp: LocalDateTime = LocalDateTime.now(),
) : ActivityEvent(
        userId,
        DISLIKE,
        targetId,
        targetName,
        shorten(commentOverview),
        timestamp,
        categoryId,
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

        fun from(interaction: CommentInteraction): CommentDislikeEvent {
            val comment = interaction.comment
            val figure = comment.figure

            return CommentDislikeEvent(
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
                commentOverview = shorten(comment.content, 20),
                commentId = comment.rootId!!,
                activityType = DISLIKE,
                categoryId = comment.figure.category.id,
            )
        }
    }
}
