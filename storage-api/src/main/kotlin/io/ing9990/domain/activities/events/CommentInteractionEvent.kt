package io.ing9990.domain.activities.events

import io.ing9990.domain.activities.ActivityEvent
import io.ing9990.domain.activities.ActivityType
import io.ing9990.domain.comment.CommentInteraction
import io.ing9990.domain.comment.InteractionType
import java.time.LocalDateTime

data class CommentInteractionEvent(
    override val userId: Long,
    override val targetId: Long,
    override val targetName: String,
    val figureId: Long,
    val interactionType: InteractionType,
    override val timestamp: LocalDateTime = LocalDateTime.now(),
) : ActivityEvent(userId, ActivityType.COMMENT_INTERACTION, targetId, targetName, null, timestamp) {
    companion object {
        fun from(interaction: CommentInteraction): CommentInteractionEvent {
            val comment = interaction.comment
            val figure = comment.figure

            return CommentInteractionEvent(
                userId =
                    interaction.user.id
                        ?: throw IllegalArgumentException("User ID is required"),
                targetId = comment.id ?: throw IllegalArgumentException("Comment ID is required"),
                targetName = figure.name,
                figureId = figure.id ?: throw IllegalArgumentException("Figure ID is required"),
                interactionType = interaction.interactionType,
            )
        }
    }
}
