package io.ing9990.domain.activities.repositories.querydsl

import io.ing9990.domain.activities.ActivityType
import io.ing9990.domain.activities.repositories.querydsl.dto.ActivityOverviewResult
import io.ing9990.domain.activities.repositories.querydsl.dto.RecentActivityResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ActivityCustomRepository {
    // 최근 활동 조회
    fun findRecentActivitiesByUserId(
        userId: Long,
        limit: Int,
    ): List<RecentActivityResult>

    // 활동 요약 조회
    fun getActivityOverviewByUserId(userId: Long): ActivityOverviewResult

    // 활동 타입별 개수 조회
    fun countByUserIdAndActivityType(
        userId: Long,
        activityType: ActivityType,
    ): Int

    // 모든 활동 페이징 조회
    fun findAllActivitiesByUserId(
        userId: Long,
        pageable: Pageable,
    ): Page<RecentActivityResult>

    // 타입별 활동 페이징 조회
    fun findActivitiesByTypeAndUserId(
        userId: Long,
        type: String,
        pageable: Pageable,
    ): Page<RecentActivityResult>
}
