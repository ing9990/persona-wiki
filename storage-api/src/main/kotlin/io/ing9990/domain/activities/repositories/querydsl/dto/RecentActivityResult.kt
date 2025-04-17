package io.ing9990.domain.activities.repositories.querydsl.dto

import io.ing9990.domain.activities.Activity
import io.ing9990.domain.activities.ActivityType
import io.ing9990.domain.activities.ActivityType.COMMENT
import io.ing9990.domain.activities.ActivityType.DISLIKED
import io.ing9990.domain.activities.ActivityType.LIKE
import io.ing9990.domain.activities.ActivityType.LIKED
import io.ing9990.domain.activities.ActivityType.REPLY
import java.time.LocalDateTime

data class RecentActivityResult(
    val activityId: Long?,
    val userId: Long?,
    val activityType: ActivityType,
    val targetId: Long,
    val categoryId: String,
    val figureName: String,
    val description: String?,
    val createdAt: LocalDateTime,
    val commentId: Long?,
) {
    companion object {
        fun from(activity: Activity): RecentActivityResult =
            RecentActivityResult(
                activityId = activity.id,
                userId = activity.user.id,
                activityType = activity.activityType,
                targetId = activity.targetId,
                figureName = activity.targetName,
                description = activity.description,
                createdAt = activity.createdAt,
                categoryId = activity.categoryId,
                commentId = activity.commentId,
            )
    }

    fun isCommentActivity() =
        listOf(
            LIKED,
            DISLIKED,
            COMMENT,
            REPLY,
            LIKE,
            DISLIKED,
        ).contains(activityType)
}
