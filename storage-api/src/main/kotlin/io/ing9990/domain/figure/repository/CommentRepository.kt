package io.ing9990.domain.figure.repository

import io.ing9990.domain.figure.Comment
import io.ing9990.domain.figure.CommentType
import io.ing9990.domain.figure.CommentType.ROOT
import io.ing9990.domain.figure.repository.querydsl.CommentCustomRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * 댓글 관련 데이터 액세스를 위한 Repository 인터페이스
 */
interface CommentRepository : JpaRepository<Comment, Long>, CommentCustomRepository {

    /**
     * 인물 ID로 최상위 댓글(원 댓글)만 조회하고, 생성일 내림차순으로 정렬합니다.
     */
    @Query(
        """
        SELECT c 
        FROM comment c 
        WHERE c.figure.id = :figureId 
        AND c.commentType = :commentType 
        ORDER BY c.createdAt DESC
    """
    )
    fun findCommentsByFigureIdAndTypeOrderByCreatedAtDesc(
        figureId: Long,
        commentType: CommentType = ROOT
    ): List<Comment>

    /**
     * 인물 ID로 최상위 댓글(원 댓글)만 페이지 단위로 조회합니다.
     * @param figureId 인물 ID
     * @param commentType 댓글 타입 (기본값: ROOT)
     * @param pageable 페이징 정보(페이지 번호, 크기, 정렬 방식 등)
     * @return 페이징된 댓글 목록
     */
    @Query(
        """
        SELECT c 
        FROM comment c
        WHERE c.figure.id = :figureId 
        AND c.commentType = :commentType
    """
    )
    fun findCommentsByFigureIdAndType(
        figureId: Long,
        commentType: CommentType = ROOT,
        pageable: Pageable,
    ): Page<Comment>

    @Query("select count(c) from comment c where c.figure.id = ?1")
    fun countCommentsByFigureId(figureId: Long): Int

    /**
     * 원래 메서드 유지 (기존 코드와의 호환성을 위해)
     */
    fun findByFigureId(
        figureId: Long,
        pageable: Pageable,
    ): Page<Comment>

    /**
     * 부모 댓글 ID로 모든 답글을 조회합니다.
     * @param parentId 부모 댓글 ID
     * @return 답글 목록
     */
    @Query(
        """
        SELECT c 
        FROM comment c 
        WHERE c.parent.id = :parentId 
        AND c.commentType = :commentType 
        ORDER BY c.createdAt ASC
    """
    )
    fun findRepliesByParentId(
        parentId: Long,
        commentType: CommentType = CommentType.REPLY
    ): List<Comment>

    /**
     * 부모 댓글 ID로 답글을 페이징하여 조회합니다.
     * @param parentId 부모 댓글 ID
     * @param commentType 댓글 타입 (기본값: REPLY)
     * @param pageable 페이징 정보
     * @return 페이징된 답글 목록
     */
    @Query(
        """
        SELECT c 
        FROM comment c 
        WHERE c.parent.id = :parentId 
        AND c.commentType = :commentType
    """
    )
    fun findRepliesByParentId(
        parentId: Long,
        commentType: CommentType = CommentType.REPLY,
        pageable: Pageable,
    ): Page<Comment>

    /**
     * 특정 댓글의 모든 답글 수를 조회합니다.
     * @param commentId 댓글 ID
     * @param commentType 댓글 타입 (기본값: REPLY)
     * @return 답글 수
     */
    @Query(
        """
        SELECT COUNT(c) 
        FROM comment c 
        WHERE c.parent.id = :commentId 
        AND c.commentType = :commentType
    """
    )
    fun countRepliesByParentId(
        commentId: Long,
        commentType: CommentType = CommentType.REPLY
    ): Long
}