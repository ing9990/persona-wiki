package io.ing9990.api.comments

import io.ing9990.aop.AuthorizedUser
import io.ing9990.aop.CurrentUser
import io.ing9990.aop.resolver.CurrentUserDto
import io.ing9990.api.comments.dto.request.CommentRequest
import io.ing9990.api.comments.dto.response.CommentInteractionResponse
import io.ing9990.api.comments.dto.response.CommentResponse
import io.ing9990.domain.comment.service.CommentService
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.user.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 댓글 및 평가 관련 REST API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/v1")
class CommentApi(
    private val figureService: FigureService,
    private val commentService: CommentService,
) {
    /**
     * 새 댓글을 추가합니다.
     */
    @PostMapping("/figure/{figureId}/comments")
    fun addComment(
        @PathVariable figureId: Long,
        @RequestBody request: CommentRequest,
        @AuthorizedUser user: User,
    ): ResponseEntity<CommentResponse> {
        val comment =
            commentService.addComment(
                figureId = figureId,
                content = request.content,
                user = user,
            )

        return ResponseEntity.ok(CommentResponse.from(comment))
    }

    @PostMapping("/comments/{commentId}/like")
    fun likeComment(
        @PathVariable commentId: Long,
        @AuthorizedUser user: User,
    ): ResponseEntity<CommentInteractionResponse> {
        val comment = commentService.likeOrDislikeComment(commentId, true, user)
        val interaction = comment.getUserInteraction(user.id!!)

        return ResponseEntity.ok(
            CommentInteractionResponse.from(
                commentId = commentId,
                interactionType = interaction?.interactionType,
            ),
        )
    }

    @PostMapping("/comments/{commentId}/dislike")
    fun dislikeComment(
        @PathVariable commentId: Long,
        @AuthorizedUser user: User,
    ): ResponseEntity<CommentInteractionResponse> {
        val comment = commentService.likeOrDislikeComment(commentId, false, user)
        val interaction = comment.getUserInteraction(user.id!!)

        return ResponseEntity.ok(
            CommentInteractionResponse.from(
                commentId = commentId,
                interactionType = interaction?.interactionType,
            ),
        )
    }

    /**
     * 댓글에 답글을 추가합니다.
     */
    @PostMapping("/comments/{commentId}/replies")
    fun addReply(
        @AuthorizedUser user: User,
        @PathVariable commentId: Long,
        @RequestBody request: CommentRequest,
    ): ResponseEntity<CommentResponse> {
        val reply =
            commentService.addReply(
                user = user,
                parentCommentId = commentId,
                content = request.content,
            )

        return ResponseEntity.ok(CommentResponse.from(reply))
    }

    /**
     * 댓글과 그에 달린 답글 목록을 조회합니다.
     */
    @GetMapping("/comments/{rootCommentId}/replies")
    fun getReplies(
        @PathVariable rootCommentId: Long,
        @CurrentUser currentUser: CurrentUserDto,
    ): ResponseEntity<List<CommentResponse>> {
        val replies =
            figureService.getRepliesWithUserInteractions(
                rootCommentId,
                currentUser.currentUser?.id ?: -1,
            )

        val repliesResponse = replies.map { CommentResponse.from(it, currentUser.getUserIdOrDefault()) }

        return ResponseEntity.ok(repliesResponse)
    }
}
