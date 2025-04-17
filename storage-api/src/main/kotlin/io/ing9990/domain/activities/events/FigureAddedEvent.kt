package io.ing9990.domain.activities.events

import io.ing9990.domain.activities.ActivityEvent
import io.ing9990.domain.activities.ActivityType
import io.ing9990.domain.figure.Figure
import java.time.LocalDateTime

// 인물 추가 이벤트
data class FigureAddedEvent(
    override val userId: Long,
    override val targetId: Long,
    override val targetName: String,
    val categoryName: String,
    override val description: String? = null,
    override val timestamp: LocalDateTime = LocalDateTime.now(),
    override val activityType: ActivityType,
    override val categoryId: String,
) : ActivityEvent(
        userId,
        ActivityType.PERSON_ADD,
        targetId,
        targetName,
        description,
        timestamp,
        categoryId = categoryId,
    ) {
    companion object {
        fun from(
            figure: Figure,
            userId: Long,
        ): FigureAddedEvent {
            val truncatedBio =
                figure.bio?.let {
                    if (it.length > 50) "${it.take(50)}..." else it
                }

            return FigureAddedEvent(
                userId = userId,
                targetId = figure.id ?: throw IllegalArgumentException("Figure ID is required"),
                targetName = figure.name,
                categoryName = figure.category.displayName,
                description = truncatedBio,
                activityType = ActivityType.PERSON_ADD,
                categoryId = figure.category.id,
            )
        }
    }
}
