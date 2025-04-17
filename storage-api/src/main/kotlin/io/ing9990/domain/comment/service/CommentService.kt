package io.ing9990.domain.comment.service

import io.ing9990.domain.activities.events.handler.ActivityEventPublisher
import io.ing9990.domain.comment.Comment
import io.ing9990.domain.comment.CommentInteraction
import io.ing9990.domain.comment.CommentType
import io.ing9990.domain.comment.InteractionType
import io.ing9990.domain.comment.repository.CommentInteractionRepository
import io.ing9990.domain.comment.repository.CommentRepository
import io.ing9990.domain.comment.repository.querydsl.dto.CommentResult
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val figureService: FigureService,
    private val commentRepository: CommentRepository,
    private val commentInteractionRepository: CommentInteractionRepository,
    private val activityEventPublisher: ActivityEventPublisher,
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

        val savedComment: Comment = commentRepository.save(comment)
        savedComment.rootItself()
        activityEventPublisher
            .publishCommentCreated(savedComment)

        return savedComment
    }

    /**
     * Root 댓글을 페이지네이션 하여 조회합니다.
     */
    @Transactional(readOnly = true)
    fun getCommentByPagination(
        userId: Long,
        figureId: Long,
        pageable: Pageable,
    ): Page<CommentResult> {
        val commentPage =
            commentRepository.findCommentsByFigureIdWithUserInteractions(
                figureId = figureId,
                userId = userId,
                pageable = pageable,
            )
        return commentPage
    }

    /**
     * 댓글에 좋아요/싫어요를 추가합니다.
     * @param commentId 댓글 ID
     * @param isLike true면 좋아요, false면 싫어요
     */
    @Transactional
    fun likeOrDislikeComment(
        commentId: Long,
        interactionType: InteractionType,
        user: User,
    ): CommentResult {
        val comment =
            commentRepository.findByIdOrNull(commentId)
                ?: throw IllegalArgumentException("해당 ID의 댓글이 존재하지 않습니다: $commentId")

        // 기존 상호작용 검색
        val existingInteraction =
            commentInteractionRepository
                .findByUserIdAndCommentId(user.id!!, commentId)

        when {
            existingInteraction?.interactionType == interactionType -> {
                comment.deleteInteraction(user.id!!, interactionType)
                commentInteractionRepository.delete(existingInteraction)
            }

            existingInteraction != null -> {
                comment.updateInteraction(user.id!!, interactionType)
            }
            // 상호작용이 없으면 새로 생성
            else -> {
                val interaction =
                    CommentInteraction(
                        user = user,
                        comment = comment,
                        interactionType = interactionType,
                    )
                comment.addInteraction(interaction)
                toEvent(interactionType, interaction)
            }
        }

        return CommentResult.from(comment)
    }

    private fun toEvent(
        interactionType: InteractionType,
        interaction: CommentInteraction,
    ) {
        if (interactionType == InteractionType.LIKE) {
            activityEventPublisher.publishCommentLike(interaction)
            activityEventPublisher.publishCommentLiked(interaction)
        } else {
            activityEventPublisher.publishCommentDislike(interaction)
        }
    }

    /**
     * 댓글에 답글을 추가합니다.
     * @param parentCommentId 부모 댓글 ID
     * @param content 답글 내용
     * @param user 답글 작성자
     * @return 생성된 답글
     */
    @Transactional
    fun addReply(
        parentCommentId: Long,
        content: String,
        user: User,
    ): CommentResult {
        val parentComment =
            commentRepository.findByIdOrNull(parentCommentId)
                ?: throw IllegalArgumentException("해당 ID의 댓글이 존재하지 않습니다: $parentCommentId")

        // 최상위 부모(root) 댓글 ID 결정
        val rootId =
            if (parentComment.isRootComment()) {
                parentComment.id
            } else {
                parentComment.rootId
            }

        // 댓글 깊이 결정 (부모 댓글의 깊이 + 1)
        val depth = parentComment.depth + 1

        // 제한된 깊이 체크 (최대 3단계까지만 허용)
        if (depth > 3) {
            throw IllegalArgumentException("더 이상 답글을 달 수 없습니다. 최대 깊이에 도달했습니다.")
        }

        // 새 답글 생성
        val reply: Comment =
            Comment(
                figure = parentComment.figure,
                content = content,
                parent = parentComment,
                depth = depth,
                rootId = rootId,
                commentType = CommentType.REPLY,
                user = user,
            )

        // 부모 댓글에 답글 추가
        parentComment.addReply(reply)
        val savedReply: Comment = commentRepository.save(reply)
        activityEventPublisher.publishCommentCreated(savedReply)

        return CommentResult.from(savedReply)
    }

    fun getReplies(
        commentId: Long,
        userId: Long,
    ): List<CommentResult> {
        val replies =
            commentRepository.findRepliesWithUserInteractions(
                parentId = commentId,
                userId = userId,
            )

        return replies
    }
}
