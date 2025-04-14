package io.ing9990.domain.activities.events

import io.ing9990.domain.activities.ActivityEvent
import io.ing9990.domain.activities.ActivityType
import io.ing9990.domain.comment.Comment
import java.time.LocalDateTime

data class ReplyCreatedEvent(
    override val userId: Long,
    override val targetId: Long,
    override val targetName: String,
    val figureId: Long,
    override val description: String? = null,
    override val timestamp: LocalDateTime = LocalDateTime.now(),
) : ActivityEvent(userId, ActivityType.REPLY, targetId, targetName, description, timestamp) {
    companion object {
        fun from(reply: Comment): ReplyCreatedEvent {
            if (!reply.isReply()) {
                throw IllegalArgumentException("Comment must be a reply")
            }

            val truncatedContent =
                if (reply.content.length > 50) {
                    "${reply.content.take(50)}..."
                } else {
                    reply.content
                }

            val parentComment =
                reply.parent
                    ?: throw IllegalArgumentException("Reply must have a parent comment")

            return ReplyCreatedEvent(
                userId = reply.user?.id ?: throw IllegalArgumentException("User ID is required"),
                targetId =
                    parentComment.id
                        ?: throw IllegalArgumentException("Parent comment ID is required"),
                targetName = reply.figure.name,
                figureId =
                    reply.figure.id
                        ?: throw IllegalArgumentException("Figure ID is required"),
                description = truncatedContent,
            )
        }
    }
}
