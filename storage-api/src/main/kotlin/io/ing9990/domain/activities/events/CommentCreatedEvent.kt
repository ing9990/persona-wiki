package io.ing9990.domain.activities.events

import io.ing9990.domain.activities.ActivityEvent
import io.ing9990.domain.activities.ActivityType
import io.ing9990.domain.comment.Comment
import java.time.LocalDateTime

data class CommentCreatedEvent(
    override val userId: Long,
    override val targetId: Long,
    override val targetName: String,
    override val description: String? = null,
    override val timestamp: LocalDateTime = LocalDateTime.now(),
) : ActivityEvent(userId, ActivityType.COMMENT, targetId, targetName, description, timestamp) {
    companion object {
        fun from(comment: Comment): CommentCreatedEvent {
            val truncatedContent =
                if (comment.content.length > 50) {
                    "${comment.content.take(50)}..."
                } else {
                    comment.content
                }

            return CommentCreatedEvent(
                userId = comment.user?.id ?: throw IllegalArgumentException("User ID is required"),
                targetId =
                    comment.figure.id
                        ?: throw IllegalArgumentException("Figure ID is required"),
                targetName = comment.figure.name,
                description = truncatedContent,
            )
        }
    }
}
