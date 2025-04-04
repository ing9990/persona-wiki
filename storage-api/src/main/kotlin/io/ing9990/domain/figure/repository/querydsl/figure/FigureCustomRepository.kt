package io.ing9990.domain.figure.repository.querydsl.figure

import io.ing9990.domain.figure.Figure

/**
 * QueryDSL을 활용한 Figure 조회 커스텀 인터페이스
 */
interface FigureCustomRepository {
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
        name: String,
    ): Figure?
}
