package io.ing9990.domain.activities.service

import io.ing9990.domain.activities.ActivityType
import io.ing9990.domain.activities.repositories.ActivityRepository
import io.ing9990.domain.activities.repositories.querydsl.dto.ActivityOverviewResult
import io.ing9990.domain.activities.repositories.querydsl.dto.RecentActivityResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
) {
    @Transactional(readOnly = true)
    fun getRecentActivities(
        userId: Long,
        limit: Int,
    ): List<RecentActivityResult> = activityRepository.findRecentActivitiesByUserId(userId, limit)

    @Transactional(readOnly = true)
    fun getActivityOverview(userId: Long): ActivityOverviewResult = activityRepository.getActivityOverviewByUserId(userId)

    @Transactional(readOnly = true)
    fun getAllActivitiesByUserId(
        userId: Long,
        pageable: Pageable,
    ): Page<RecentActivityResult> = activityRepository.findAllActivitiesByUserId(userId, pageable)

    @Transactional(readOnly = true)
    fun getActivitiesByTypeAndUserId(
        userId: Long,
        type: String,
        pageable: Pageable,
    ): Page<RecentActivityResult> = activityRepository.findActivitiesByTypeAndUserId(userId, type, pageable)

    @Transactional(readOnly = true)
    fun getActivityTypes(): List<ActivityTypeResult> =
        ActivityType.values().map {
            ActivityTypeResult(it.name, getDisplayName(it))
        }

    private fun getDisplayName(activityType: ActivityType): String =
        when (activityType) {
            ActivityType.COMMENT -> "댓글"
            ActivityType.REPLY -> "답글"
            ActivityType.VOTE -> "투표"
            ActivityType.PERSON_ADD -> "인물 추가"
            ActivityType.PERSON_EDIT -> "인물 수정"
            ActivityType.LIKE -> "댓글 좋아요"
            ActivityType.LIKED -> "댓글 좋아요 받음"
            ActivityType.DISLIKE -> "댓글 싫어요"
            ActivityType.DISLIKED -> "댓글 싫어요 받음"
        }
}
