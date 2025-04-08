package io.ing9990.api.comments

import io.ing9990.aop.AuthorizedUser
import io.ing9990.aop.CurrentUser
import io.ing9990.aop.resolver.CurrentUserDto
import io.ing9990.api.comments.dto.request.CommentRequest
import io.ing9990.api.comments.dto.response.CommentPageResponse
import io.ing9990.api.comments.dto.response.CommentResponse
import io.ing9990.domain.comment.repository.querydsl.dto.CommentResult
import io.ing9990.domain.comment.service.CommentService
import io.ing9990.domain.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
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
@RequestMapping("/api/v1/figures/{figureId}/comments")
class CommentApi(
    private val commentService: CommentService,
) {
    /**
     * 특정 인물의 댓글 목록을 페이징하여 조회
     * @param figureId 인물 ID
     * @param pageable 페이지 정보 (page, size)
     * @return 페이징된 댓글 목록
     */
    @GetMapping
    fun getComments(
        @CurrentUser currentUser: CurrentUserDto,
        @PathVariable figureId: Long,
        @PageableDefault(page = 0, size = 10) pageable: Pageable,
    ): ResponseEntity<CommentPageResponse> {
        val userId = currentUser.getUserIdOrDefault()

        val result: Page<CommentResult> =
            commentService.getCommentByPagination(
                figureId = figureId,
                userId = userId,
                pageable = pageable,
            )

        val response: CommentPageResponse =
            CommentPageResponse.from(result)

        return ResponseEntity.ok(response)
    }

    /**
     * 새 댓글을 추가합니다.
     */
    @PostMapping
    fun addComment(
        @PathVariable figureId: Long,
        @RequestBody request: CommentRequest,
        @AuthorizedUser user: User,
    ): ResponseEntity<CommentResponse> {
        val addComment =
            commentService.addComment(
                figureId = figureId,
                content = request.content,
                user = user,
            )

        return ResponseEntity.status(HttpStatus.CREATED).body(
            CommentResponse.from(
                CommentResult.from(addComment),
            ),
        )
    }

    /**
     * 특정 댓글에 대한 답글 목록 조회
     * @param commentId 댓글 ID
     * @return 답글 목록
     */
    @GetMapping("/{commentId}/replies")
    fun getReplies(
        @CurrentUser currentUser: CurrentUserDto,
        @PathVariable commentId: Long,
    ): ResponseEntity<List<CommentResponse>> {
        val userId = currentUser.getUserIdOrDefault()

        val response: List<CommentResponse> =
            commentService
                .getReplies(commentId, userId)
                .map { CommentResponse.from(it) }

        return ResponseEntity.ok(response)
    }

    /**
     * 댓글에 좋아요 혹은 싫어요를 누릅니다.
     */
    @PostMapping("/{commentId}/toggle")
    fun likeComment(
        @AuthorizedUser user: User,
        @PathVariable commentId: Long,
        @PathVariable figureId: Long,
    ): ResponseEntity<Unit> {
        commentService.likeOrDislikeComment(commentId, true, user)

        return ResponseEntity.noContent().build()
    }

    /**
     * 댓글에 답글을 추가합니다.
     * @RequestMapping 주소로 인해 이 API의 주소는
     * /api/v1/figures/{figureId}/comments/{commentId}/replies 입니다.
     */
    @PostMapping("/{commentId}/replies")
    fun addReply(
        @AuthorizedUser user: User,
        @PathVariable commentId: Long,
        @RequestBody request: CommentRequest,
        // 쓰지 않아도 무관함. 리소스 구조상 받는 거임.
        @PathVariable figureId: Long,
    ): ResponseEntity<Unit> {
        commentService.addReply(
            user = user,
            parentCommentId = commentId,
            content = request.content,
        )

        return ResponseEntity.noContent().build()
    }
}
