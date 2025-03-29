package io.ing9990.domain.figure.repository

import io.ing9990.domain.figure.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 댓글 관련 데이터 액세스를 위한 Repository 인터페이스
 */
interface CommentRepository : JpaRepository<Comment, Long> {
    /**
     * 인물 ID로 댓글을 조회하고, 생성일 내림차순으로 정렬합니다.
     */
    fun findByFigureIdOrderByCreatedAtDesc(figureId: Long): List<Comment>

    /**
     * 인물 ID로 댓글을 페이지 단위로 조회합니다.
     * @param figureId 인물 ID
     * @param pageable 페이징 정보(페이지 번호, 크기, 정렬 방식 등)
     * @return 페이징된 댓글 목록
     */
    fun findByFigureId(
        figureId: Long,
        pageable: Pageable,
    ): Page<Comment>
}
