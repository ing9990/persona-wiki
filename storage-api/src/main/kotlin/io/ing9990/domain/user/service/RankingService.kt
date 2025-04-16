package io.ing9990.domain.user.service

import io.ing9990.domain.user.User
import io.ing9990.domain.user.repositories.querydsl.RankingRepository
import io.ing9990.domain.user.service.dto.RankingPageResult
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RankingService(
    private val rankingRepository: RankingRepository,
) {
    /**
     * 랭킹 페이지에 필요한 모든 데이터를 한번에 조회합니다.
     *
     * @param limit 가져올 최대 랭킹 수
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 랭킹 페이지에 필요한 모든 데이터를 포함한 결과 객체
     */
    @Transactional(readOnly = true)
    fun rankingTopLimit(
        limit: Int,
        page: Int,
        size: Int,
    ): RankingPageResult {
        // 유효성 검사
        val validLimit = if (limit <= 0) 100 else limit
        val validPage = if (page < 0) 0 else page
        val validSize = if (size <= 0) 20 else size

        // QueryDSL을 통해 랭킹 페이지 데이터 조회
        return rankingRepository.getRankingPageData(
            page = validPage,
            size = validSize,
            limit = validLimit,
        )
    }

    /**
     * 특정 사용자의 랭킹 순위를 조회합니다.
     */
    @Transactional(readOnly = true)
    fun getUserRank(userId: Long): Int = rankingRepository.getUserRank(userId)

    /**
     * 사용자 정보를 저장합니다.
     */
    @Transactional
    fun save(user: User): User = rankingRepository.save(user)
}
