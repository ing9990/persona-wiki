package io.ing9990.web.controller.fragments.comment

import io.ing9990.aop.AuthorizedUser
import io.ing9990.aop.CurrentUser
import io.ing9990.aop.resolver.CurrentUserDto
import io.ing9990.api.comments.dto.response.CommentPageResponse
import io.ing9990.api.comments.dto.response.CommentResponse
import io.ing9990.domain.comment.service.CommentService
import io.ing9990.domain.user.User
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/figures/{figureId}/comments/fragment")
class CommentFragmentController(
    private val commentService: CommentService,
) {
    /**
     * 답글 추가 후 해당 댓글의 답글 목록 다시 렌더링
     */
    @PostMapping("/{commentId}/replies")
    fun addReplyFragment(
        @AuthorizedUser user: User,
        @PathVariable commentId: Long,
        @RequestParam content: String,
        model: Model,
    ): String {
        commentService.addReply(
            user = user,
            parentCommentId = commentId,
            content = content,
        )

        val replies =
            commentService
                .getReplies(
                    commentId = commentId,
                    userId = user.id!!,
                ).map {
                    io.ing9990.api.comments.dto.response.CommentResponse
                        .from(it)
                }

        model.addAttribute("replies", replies)
        model.addAttribute("commentId", commentId)
        return "fragments/comments/reply-list :: replyList"
    }

    /**
     * 댓글 좋아요/싫어요 토글함
     */
    @ResponseBody
    @PostMapping("/{commentId}/toggle")
    fun toggleCommentFragment(
        @AuthorizedUser user: User,
        @PathVariable commentId: Long,
        @PathVariable figureId: Long,
        model: Model,
    ): ResponseEntity<Unit> {
        commentService.likeOrDislikeComment(
            commentId = commentId,
            isLike = true,
            user = user,
        )

        return ResponseEntity.noContent().build()
    }

    /**
     * 인물 디테일 페이지 - 댓글 무한스크롤 구현
     */
    @GetMapping
    fun getCommentFragment(
        @PathVariable figureId: Long,
        @RequestParam page: Int,
        @RequestParam size: Int,
        model: Model,
        @CurrentUser currentUser: CurrentUserDto,
    ): String {
        val result =
            commentService.getCommentByPagination(
                figureId = figureId,
                userId = currentUser.getUserIdOrDefault(),
                pageable = PageRequest.of(page, size),
            )

        val pageResponse: CommentPageResponse =
            CommentPageResponse.from(result)

        model.addAttribute("comments", pageResponse)
        model.addAttribute("figureId", figureId)

        return "fragments/comments/comment-list :: commentList"
    }

    /**
     * 답글 목록 Fragment 반환
     */
    @GetMapping("/{commentId}/replies")
    fun getRepliesFragment(
        @CurrentUser currentUser: CurrentUserDto,
        @PathVariable figureId: Long,
        @PathVariable commentId: Long,
        model: Model,
    ): String {
        val userId = currentUser.getUserIdOrDefault()
        val replies =
            commentService
                .getReplies(commentId, userId)
                .map { CommentResponse.from(it) }

        model.addAttribute("replies", replies)
        model.addAttribute("figureId", figureId)

        return "fragments/comments/comment-replies :: replies"
    }
}
