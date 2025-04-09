package io.ing9990.domain.figure.repository.querydsl

import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.service.dto.FigureCardResult
import io.ing9990.domain.figure.service.dto.FigureDetailsResult

/**
 * QueryDSL을 활용한 Figure 조회 커스텀 인터페이스
 */
interface FigureCustomRepository {
    /**
     * 검색 창에서 인물 검색 시 응답임 LIKE 문으로 조회
     */
    fun searchByName(name: String): List<FigureCardResult>

    /**
     * 주목받는 인물들, 투표 수와 댓글 수가 가장 많은 사람부터 5개 가져옵니다.
     */
    fun findPopularFigues(): List<FigureCardResult>

    /**
     * 카테고리와 함께 인기 순서로 조회합니다.
     */
    fun findAllWithCategoryPopularOrder(): List<Figure>

    /**
     * ID로 인물을 조회하며 투표 정보를 함께 로드합니다.
     */
    fun findByIdWithVotes(id: Long): Figure?

    /**
     * ID로 인물을 조회하며 카테고리, 투표, 댓글 정보를 함께 로드합니다.
     */
    fun findByIdWithDetails(id: Long): Figure?

    /**
     * 카테고리 ID와 이름으로 인물을 조회하며 모든 연관 정보를 함께 로드합니다.
     */
    fun findByCategoryIdAndNameWithDetails(
        categoryId: String,
        figureName: String,
        userId: Long,
        commentPage: Int,
        commentSize: Int,
    ): FigureDetailsResult
}
