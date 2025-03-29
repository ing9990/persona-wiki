package io.ing9990.api.comments

import io.ing9990.api.comments.dto.request.CommentRequest
import io.ing9990.api.comments.dto.response.CommentResponse
import io.ing9990.domain.figure.service.FigureService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
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
     * 댓글에 좋아요를 추가합니다.
     */
    @PostMapping("/comments/{commentId}/like")
    fun likeComment(
        @PathVariable commentId: Long,
    ): ResponseEntity<String> {
        figureService.likeOrDislikeComment(commentId, true)
        return ResponseEntity.ok("좋아요가 추가되었습니다.")
    }

    /**
     * 댓글에 싫어요를 추가합니다.
     */
    @PostMapping("/comments/{commentId}/dislike")
    fun dislikeComment(
        @PathVariable commentId: Long,
    ): ResponseEntity<String> {
        figureService.likeOrDislikeComment(commentId, false)
        return ResponseEntity.ok("싫어요가 추가되었습니다.")
    }
}
