package io.ing9990.domain.comment.service

import io.ing9990.domain.comment.Comment
import io.ing9990.domain.comment.CommentInteraction
import io.ing9990.domain.comment.CommentType
import io.ing9990.domain.comment.InteractionType
import io.ing9990.domain.comment.repository.CommentInteractionRepository
import io.ing9990.domain.comment.repository.CommentRepository
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val figureService: FigureService,
    private val commentRepository: CommentRepository,
    private val commentInteractionRepository: CommentInteractionRepository,
) {
    /**
     * 새로운 댓글을 추가합니다.
     * @param figureId 인물 ID
     * @param content 댓글 내용
     * @param user 댓글 작성자
     */
    @Transactional
    fun addComment(
        figureId: Long,
        content: String,
        user: User,
    ): Comment {
        val figure = figureService.findById(figureId)

        val comment =
            Comment(
                figure = figure,
                content = content,
                user = user,
            )

        return commentRepository.save(comment)
    }

    /**
     * 원 댓글과 그에 속한 모든 답글을 조회합니다.
     * QueryDSL을 사용하여 한 번의 쿼리로 원 댓글과 답글들을 함께 가져옵니다.
     *
     * @param rootCommentId 원 댓글 ID
     * @return 원 댓글과 모든 답글이 포함된 Comment 객체
     */
    fun getCommentWithReplies(rootCommentId: Long): List<Comment> {
        val comment = commentRepository.findWithRepliesById(rootCommentId)
        return comment?.replies ?: emptyList()
    }

    /**
     * 특정 인물에 대한 댓글 트리(원 댓글과 답글)를 페이지 단위로 조회합니다.
     * @param figureId 인물 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 댓글 트리 목록
     */
    @Transactional(readOnly = true)
    fun getCommentTreesByFigureId(
        figureId: Long,
        page: Int,
        size: Int,
    ): Page<Comment> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))

        // 원 댓글만 페이징하여 조회
        return commentRepository.findCommentsByFigureIdAndType(
            figureId = figureId,
            commentType = CommentType.ROOT,
            pageable = pageable,
        )
    }

    /**
     * 댓글에 좋아요/싫어요를 추가합니다.
     * @param commentId 댓글 ID
     * @param isLike true면 좋아요, false면 싫어요
     */
    @Transactional
    fun likeOrDislikeComment(
        commentId: Long,
        isLike: Boolean,
        user: User,
    ): Comment {
        val comment =
            commentRepository.findByIdOrNull(commentId)
                ?: throw IllegalArgumentException("해당 ID의 댓글이 존재하지 않습니다: $commentId")

        val interactionType = if (isLike) InteractionType.LIKE else InteractionType.DISLIKE

        // 기존 상호작용 검색
        val existingInteraction =
            commentInteractionRepository.findByUserIdAndCommentId(user.id!!, commentId)

        if (existingInteraction != null) {
            // 이미 같은 타입이면 아무것도 하지 않음
            if (existingInteraction.interactionType == interactionType) {
                return comment
            }

            // 다른 타입이면 업데이트
            comment.updateInteraction(user.id!!, interactionType)
        } else {
            // 새 상호작용 추가
            val interaction =
                CommentInteraction(
                    user = user,
                    comment = comment,
                    interactionType = interactionType,
                )
            comment.addInteraction(interaction)
        }

        return comment
    }
}
