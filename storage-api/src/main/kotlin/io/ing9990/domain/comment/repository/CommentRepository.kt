package io.ing9990.domain.comment.repository

import io.ing9990.domain.comment.Comment
import io.ing9990.domain.comment.CommentType
import io.ing9990.domain.comment.repository.querydsl.CommentCustomRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * 댓글 관련 데이터 액세스를 위한 Repository 인터페이스
 */
interface CommentRepository :
    JpaRepository<Comment, Long>,
    CommentCustomRepository {
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
    """,
    )
    fun findCommentsByFigureIdAndTypeOrderByCreatedAtDesc(
        figureId: Long,
        commentType: CommentType = CommentType.ROOT,
    ): List<Comment>

    /**
     * 인물 ID로 최상위 댓글(원 댓글)만 페이지 단위로 조회합니다.
     */
    @Query(
        """
        SELECT c 
        FROM comment c
        WHERE c.figure.id = :figureId 
        AND c.commentType = :commentType
    """,
    )
    fun findCommentsByFigureIdAndType(
        figureId: Long,
        commentType: CommentType = CommentType.ROOT,
        pageable: Pageable,
    ): Page<Comment>

    /**
     * 인물 ID로 모든 댓글을 페이징하여 조회합니다.
     */
    fun findByFigureId(
        figureId: Long,
        pageable: Pageable,
    ): Page<Comment>

    /**
     * 부모 댓글 ID로 모든 답글을 조회합니다.
     */
    @Query(
        """
        SELECT c 
        FROM comment c 
        WHERE c.parent.id = :parentId 
        AND c.commentType = :commentType 
        ORDER BY c.createdAt ASC
    """,
    )
    fun findRepliesByParentId(
        parentId: Long,
        commentType: CommentType = CommentType.REPLY,
    ): List<Comment>

    /**
     * 부모 댓글 ID로 답글을 페이징하여 조회합니다.
     */
    @Query(
        """
        SELECT c 
        FROM comment c 
        WHERE c.parent.id = :parentId 
        AND c.commentType = :commentType
    """,
    )
    fun findRepliesByParentId(
        parentId: Long,
        commentType: CommentType = CommentType.REPLY,
        pageable: Pageable,
    ): Page<Comment>

    /**
     * 특정 댓글의 모든 답글 수를 조회합니다.
     */
    @Query(
        """
        SELECT COUNT(c) 
        FROM comment c 
        WHERE c.parent.id = :commentId 
        AND c.commentType = :commentType
    """,
    )
    fun countRepliesByParentId(
        commentId: Long,
        commentType: CommentType = CommentType.REPLY,
    ): Long
}
