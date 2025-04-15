package io.ing9990.domain.user.repositories.querydsl

import io.ing9990.domain.user.service.dto.RankingPageResult

/**
 * QueryDSL을 사용한 랭킹 조회 인터페이스
 */
interface RankingCustomRepository {
    /**
     * 랭킹 페이지에 필요한 모든 데이터를 한번에 조회합니다.
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 랭킹 페이지에 필요한 모든 데이터를 포함한 결과 객체
     */
    fun getRankingPageData(
        page: Int,
        size: Int,
        limit: Int,
    ): RankingPageResult
}
