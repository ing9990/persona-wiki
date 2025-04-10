package io.ing9990.domain.activities.repositories.querydsl

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import io.ing9990.domain.activities.ActivityType
import io.ing9990.domain.activities.querydsl.QActivity
import io.ing9990.domain.activities.repositories.querydsl.dto.ActivityOverviewResult
import io.ing9990.domain.activities.repositories.querydsl.dto.RecentActivityResult
import io.ing9990.domain.activities.repository.querydsl.ActivityCustomRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class ActivityCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ActivityCustomRepository {
    private val activity = QActivity.activity

    override fun findRecentActivitiesByUserId(
        userId: Long,
        limit: Int,
    ): List<RecentActivityResult> =
        queryFactory
            .select(
                Projections.constructor(
                    RecentActivityResult::class.java,
                    activity.id,
                    activity.user.id,
                    activity.activityType,
                    activity.targetId,
                    activity.targetName,
                    activity.description,
                    activity.createdAt,
                ),
            ).from(activity)
            .where(activity.user.id.eq(userId))
            .orderBy(activity.createdAt.desc())
            .limit(limit.toLong())
            .fetch()

    override fun getActivityOverviewByUserId(userId: Long): ActivityOverviewResult {
        val commentCount = countByUserIdAndActivityType(userId, ActivityType.COMMENT)
        val voteCount = countByUserIdAndActivityType(userId, ActivityType.VOTE)
        val personAddCount = countByUserIdAndActivityType(userId, ActivityType.PERSON_ADD)

        return ActivityOverviewResult.of(commentCount, voteCount, personAddCount)
    }

    override fun countByUserIdAndActivityType(
        userId: Long,
        activityType: ActivityType,
    ): Int =
        queryFactory
            .select(activity.count().intValue())
            .from(activity)
            .where(
                activity.user.id
                    .eq(userId)
                    .and(activity.activityType.eq(activityType)),
            ).fetchOne() ?: 0

    override fun findAllActivitiesByUserId(
        userId: Long,
        pageable: Pageable,
    ): Page<RecentActivityResult> {
        val query =
            queryFactory
                .select(
                    Projections.constructor(
                        RecentActivityResult::class.java,
                        activity.id,
                        activity.user.id,
                        activity.activityType,
                        activity.targetId,
                        activity.targetName,
                        activity.description,
                        activity.createdAt,
                    ),
                ).from(activity)
                .where(activity.user.id.eq(userId))
                .orderBy(activity.createdAt.desc())

        val activities =
            query
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()

        val total =
            queryFactory
                .select(activity.count())
                .from(activity)
                .where(activity.user.id.eq(userId))
                .fetchOne() ?: 0L

        return PageImpl(activities, pageable, total)
    }

    override fun findActivitiesByTypeAndUserId(
        userId: Long,
        type: String,
        pageable: Pageable,
    ): Page<RecentActivityResult> {
        val activityType =
            try {
                ActivityType.valueOf(type)
            } catch (e: IllegalArgumentException) {
                null
            }

        val whereClause =
            activity.user.id
                .eq(userId)
                .and(activityTypeEq(activityType))

        val query =
            queryFactory
                .select(
                    Projections.constructor(
                        RecentActivityResult::class.java,
                        activity.id,
                        activity.user.id,
                        activity.activityType,
                        activity.targetId,
                        activity.targetName,
                        activity.description,
                        activity.createdAt,
                    ),
                ).from(activity)
                .where(whereClause)
                .orderBy(activity.createdAt.desc())

        val activities =
            query
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()

        val total =
            queryFactory
                .select(activity.count())
                .from(activity)
                .where(whereClause)
                .fetchOne() ?: 0L

        return PageImpl(activities, pageable, total)
    }

    private fun activityTypeEq(activityType: ActivityType?): BooleanExpression? =
        if (activityType != null) activity.activityType.eq(activityType) else null
}
