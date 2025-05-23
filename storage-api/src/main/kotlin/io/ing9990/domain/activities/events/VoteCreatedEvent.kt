package io.ing9990.domain.activities.events

import io.ing9990.domain.activities.ActivityEvent
import io.ing9990.domain.activities.ActivityType
import io.ing9990.domain.vote.Vote
import java.time.LocalDateTime

data class VoteCreatedEvent(
    override val userId: Long,
    override val targetId: Long,
    override val targetName: String,
    override val activityType: ActivityType,
    override val categoryId: String,
    val sentiment: String,
    override val timestamp: LocalDateTime = LocalDateTime.now(),
) : ActivityEvent(
        userId,
        ActivityType.VOTE,
        targetId,
        targetName,
        null,
        timestamp,
        categoryId = categoryId,
    ) {
    companion object {
        fun from(vote: Vote): VoteCreatedEvent =
            VoteCreatedEvent(
                userId = vote.user.id ?: throw IllegalArgumentException("User ID is required"),
                targetId =
                    vote.figure.id
                        ?: throw IllegalArgumentException("Figure ID is required"),
                targetName = vote.figure.name,
                sentiment = vote.sentiment.name,
                activityType = ActivityType.VOTE,
                categoryId = vote.figure.category.id,
            )
    }
}
