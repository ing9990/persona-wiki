package io.ing9990.domain.user.repositories.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import io.ing9990.domain.user.querydsl.QUser
import io.ing9990.domain.user.service.dto.RankingPageResult
import io.ing9990.domain.user.service.dto.UserRankingInfo
import org.springframework.stereotype.Repository

@Repository
class RankingCustomRepositoryImpl(
    private val query: JPAQueryFactory,
) : RankingCustomRepository {
    /**
     * 랭킹 페이지에 필요한 모든 데이터를 한번에 조회합니다.
     */
    override fun getRankingPageData(
        page: Int,
        size: Int,
        limit: Int,
    ): RankingPageResult {
        val user = QUser.user

        // 총 사용자 수 조회
        val totalUsers =
            query
                .select(user.count())
                .from(user)
                .fetchOne() ?: 0L

        // 전체 페이지 수 계산
        val totalPages = calculateTotalPages(totalUsers, size)

        // 상위 사용자 조회 (별도 쿼리로 최적화)
        val topUsers = getTopUsers(3)

        // 현재 페이지에 해당하는 사용자 랭킹 조회
        val pageUsers = getCurrentPageUsers(page, size)

        return RankingPageResult(
            rankings = pageUsers,
            totalUsers = totalUsers,
            totalPages = totalPages,
            currentPage = page,
            pageSize = size,
            topUsers = topUsers,
        )
    }

    /**
     * 상위 N명의 사용자를 조회합니다.
     */
    private fun getTopUsers(count: Int): List<UserRankingInfo> {
        val user = QUser.user

        val topUsers =
            query
                .selectFrom(user)
                .orderBy(user.prestige.desc())
                .limit(count.toLong())
                .fetch()

        return topUsers.mapIndexed { index, u ->
            UserRankingInfo.from(u, index + 1)
        }
    }

    /**
     * 현재 페이지에 해당하는 사용자 랭킹을 조회합니다.
     */
    private fun getCurrentPageUsers(
        page: Int,
        size: Int,
    ): List<UserRankingInfo> {
        val user = QUser.user

        val startRank = page * size

        val pageUsers =
            query
                .selectFrom(user)
                .orderBy(user.prestige.desc())
                .offset(startRank.toLong())
                .limit(size.toLong())
                .fetch()

        return pageUsers.mapIndexed { index, u ->
            UserRankingInfo.from(u, startRank + index + 1)
        }
    }

    /**
     * 전체 페이지 수를 계산합니다.
     */
    private fun calculateTotalPages(
        totalUsers: Long,
        pageSize: Int,
    ): Int =
        if (totalUsers % pageSize == 0L) {
            (totalUsers / pageSize).toInt()
        } else {
            (totalUsers / pageSize + 1).toInt()
        }
}
