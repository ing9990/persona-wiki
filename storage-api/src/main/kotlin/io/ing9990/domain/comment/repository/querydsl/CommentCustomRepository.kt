package io.ing9990.domain.comment.repository.querydsl

import io.ing9990.domain.comment.repository.querydsl.dto.CommentResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * QueryDSL을 활용한 Comment 조회 커스텀 인터페이스
 */
interface CommentCustomRepository {
    /**
     * 댓글을 페이징하여 조회합니다.
     */
    fun findCommentsByFigureIdWithUserInteractions(
        figureId: Long,
        userId: Long?,
        pageable: Pageable,
    ): Page<CommentResult>

    fun countCommentsByFigureId(figureId: Long): Long

    fun findCommentsWithUserInteractions(
        figureId: Long,
        userId: Long,
        pageable: Pageable,
    ): Page<CommentResult>

    fun findRepliesWithUserInteractions(
        parentId: Long,
        userId: Long?,
    ): List<CommentResult>
}
