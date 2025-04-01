package io.ing9990.api.comments

import io.ing9990.api.comments.dto.request.CommentRequest
import io.ing9990.api.comments.dto.response.CommentPageResponse
import io.ing9990.api.comments.dto.response.CommentResponse
import io.ing9990.domain.figure.Comment
import io.ing9990.domain.figure.service.FigureService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 댓글 및 평가 관련 REST API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api")
class CommentApi(
    private val figureService: FigureService,
) {
    /**
     * 새 댓글을 추가합니다.
     */
    @PostMapping("/figure/{figureId}/comments")
    fun addComment(
        @PathVariable figureId: Long,
        @RequestBody request: CommentRequest,
    ): ResponseEntity<CommentResponse> {
        val comment =
            figureService.addComment(
                figureId = figureId,
                content = request.content,
            )

        return ResponseEntity.ok(CommentResponse.from(comment))
    }

    /**
     * 인물에 대한 댓글을 페이지 단위로 조회합니다.
     */
    @GetMapping("/figure/{figureId}/comments")
    fun getComments(
        @PathVariable figureId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<CommentPageResponse> {
        val commentPage = figureService.getCommentsByFigureId(figureId, page, size)

        return ResponseEntity.ok(
            CommentPageResponse(
                content = commentPage.content.map { CommentResponse.from(it) },
                totalPages = commentPage.totalPages,
                totalElements = commentPage.totalElements,
                currentPage = commentPage.number,
                size = commentPage.size,
                isFirst = commentPage.isFirst,
                isLast = commentPage.isLast,
            ),
        )
    }

    /**
     * 댓글에 좋아요를 추가합니다.
     */
    @PostMapping("/comments/{commentId}/like")
    fun likeComment(
        @PathVariable commentId: Long,
    ): ResponseEntity<Map<String, Int>> {
        val comment = figureService.likeOrDislikeComment(commentId, true)
        return ResponseEntity.ok(mapOf("likes" to comment.likes, "dislikes" to comment.dislikes))
    }

    /**
     * 댓글에 싫어요를 추가합니다.
     */
    @PostMapping("/comments/{commentId}/dislike")
    fun dislikeComment(
        @PathVariable commentId: Long,
    ): ResponseEntity<Map<String, Int>> {
        val comment = figureService.likeOrDislikeComment(commentId, false)
        return ResponseEntity.ok(mapOf("likes" to comment.likes, "dislikes" to comment.dislikes))
    }

    // backend/src/main/kotlin/io/ing9990/api/comments/CommentApi.kt에 추가할 메서드

    /**
     * 댓글에 답글을 추가합니다.
     */
    @PostMapping("/comments/{commentId}/replies")
    fun addReply(
        @PathVariable commentId: Long,
        @RequestBody request: CommentRequest,
    ): ResponseEntity<CommentResponse> {
        val reply =
            figureService.addReply(
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
    ): ResponseEntity<List<CommentResponse>> {
        val replies: List<Comment> = figureService.getCommentWithReplies(rootCommentId)

        val repliesResponse = replies.map { CommentResponse.from(it) }

        return ResponseEntity.ok(repliesResponse)
    }

    /**
     * 인물에 대한 댓글과 답글을 계층 구조로 조회합니다.
     */
    @GetMapping("/figure/{figureId}/comment-trees")
    fun getCommentTrees(
        @PathVariable figureId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<CommentPageResponse> {
        val commentPage = figureService.getCommentTreesByFigureId(figureId, page, size)

        return ResponseEntity.ok(
            CommentPageResponse(
                content = commentPage.content.map { CommentResponse.from(it) },
                totalPages = commentPage.totalPages,
                totalElements = commentPage.totalElements,
                currentPage = commentPage.number,
                size = commentPage.size,
                isFirst = commentPage.isFirst,
                isLast = commentPage.isLast,
            ),
        )
    }
}
