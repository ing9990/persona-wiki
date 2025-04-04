package io.ing9990.domain.figure.repository.querydsl.comment

import io.ing9990.domain.figure.Comment

/**
 * QueryDSL을 활용한 Comment 조회 커스텀 인터페이스
 * @NoRepositoryBean은 제거 - Spring의 최신 버전에서는 필요하지 않음
 */
interface CommentCustomRepository {
    /**
     * 댓글 ID로 해당 댓글과 모든 답글을 함께 조회합니다.
     */
    fun findWithRepliesById(commentId: Long): Comment?
}
