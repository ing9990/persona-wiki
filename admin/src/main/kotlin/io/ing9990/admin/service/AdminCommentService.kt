package io.ing9990.admin.service

import io.ing9990.admin.dto.CommentDto
import io.ing9990.admin.repository.AdminCommentRepository
import io.ing9990.admin.repository.AdminFigureRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 어드민 페이지의 댓글 관리 기능을 제공하는 서비스
 */
@Service
class AdminCommentService(
    private val commentRepository: AdminCommentRepository,
    private val figureRepository: AdminFigureRepository,
) {
    /**
     * 전체 댓글 개수를 조회한다.
     * @return 댓글 개수
     */
    fun getCommentCount(): Long {
        return commentRepository.count()
    }

    /**
     * 인물별 댓글 목록을 페이징하여 조회한다.
     * @param figureId 인물 ID
     * @param pageable 페이징 정보
     * @return 댓글 페이지
     */
    fun getCommentsByFigureId(
        figureId: Long,
        pageable: Pageable,
    ): Page<CommentDto> {
        val commentsPage = commentRepository.findByFigureId(figureId, pageable)
        return commentsPage.map { CommentDto.from(it) }
    }

    /**
     * 모든 댓글을 페이징하여 조회한다.
     * @param pageable 페이징 정보
     * @return 댓글 페이지
     */
    fun getCommentsPaged(pageable: Pageable): Page<CommentDto> {
        val commentsPage = commentRepository.findAll(pageable)
        return commentsPage.map { CommentDto.from(it) }
    }

    /**
     * 댓글을 상세 조회한다.
     * @param commentId 댓글 ID
     * @return 댓글 정보
     */
    fun getCommentById(commentId: Long): CommentDto? {
        val comment = commentRepository.findByIdOrNull(commentId) ?: return null
        return CommentDto.from(comment)
    }

    /**
     * 댓글을 삭제한다.
     * @param commentId 댓글 ID
     * @throws IllegalArgumentException 댓글이 존재하지 않는 경우
     */
    @Transactional
    fun deleteComment(commentId: Long) {
        val comment =
            commentRepository.findByIdOrNull(commentId)
                ?: throw IllegalArgumentException("해당 ID의 댓글이 존재하지 않습니다: $commentId")

        commentRepository.delete(comment)
    }

    /**
     * 최근 댓글 목록을 조회한다.
     * @param limit 조회할 댓글 수
     * @return 최근 추가된 댓글 목록
     */
    fun getRecentComments(limit: Int): List<CommentDto> {
        // PageRequest.of()를 사용하여 페이지 번호와 페이지 크기, 정렬 정보를 함께 지정
        val pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        val commentsPage = commentRepository.findAll(pageable)
        return commentsPage.map { CommentDto.from(it) }.content
    }

    /**
     * 인물별 댓글 개수를 조회한다.
     * @param figureId 인물 ID
     * @return 댓글 개수
     */
    fun getCommentCountByFigureId(figureId: Long): Long {
        return commentRepository.countByFigureId(figureId)
    }
}
