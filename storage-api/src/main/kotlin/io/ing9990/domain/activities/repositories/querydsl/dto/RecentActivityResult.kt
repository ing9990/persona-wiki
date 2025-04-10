package io.ing9990.domain.activities.repositories.querydsl.dto

import io.ing9990.domain.activities.Activity
import io.ing9990.domain.activities.ActivityType
import java.time.LocalDateTime

data class RecentActivityResult(
    val activityId: Long?,
    val userId: Long?,
    val activityType: ActivityType,
    val targetId: Long,
    val targetName: String,
    val description: String?,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(activity: Activity): RecentActivityResult =
            RecentActivityResult(
                activityId = activity.id,
                userId = activity.user.id,
                activityType = activity.activityType,
                targetId = activity.targetId,
                targetName = activity.targetName,
                description = activity.description,
                createdAt = activity.createdAt,
            )
    }
}
