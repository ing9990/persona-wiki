package io.ing9990.web.controller.figures

import io.ing9990.aop.AuthorizedUser
import io.ing9990.common.Redirect
import io.ing9990.domain.comment.service.CommentService
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.user.User
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class CommentController(
    private val figureService: FigureService,
    private val commentService: CommentService,
) {
    /**
     * 인물에 대한 새 댓글을 추가합니다.
     */
    @PostMapping("/{categoryId}/@{slug}/comment")
    fun addComment(
        @PathVariable categoryId: String,
        @PathVariable slug: String,
        @RequestParam content: String,
        @AuthorizedUser user: User,
        redirectAttributes: RedirectAttributes,
    ): String {
        if (content.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "댓글 내용을 입력해주세요.")
            return Redirect.to(categoryId, slug)
        }

        // 인물 찾기
        val figure = figureService.searchByCategoryIdAndName(categoryId, slug)

        // 댓글 추가 (사용자 정보 전달)
        commentService.addComment(figure.id!!, content, user)

        // 성공 메시지
        redirectAttributes.addFlashAttribute("success", "댓글이 성공적으로 등록되었습니다.")

        return Redirect.to(categoryId, slug)
    }

    /**
     * 댓글에 답글을 추가합니다.
     */
    @PostMapping("/{categoryId}/@{figureName}/comment/{commentId}/reply")
    fun addReply(
        @PathVariable categoryId: String,
        @PathVariable figureName: String,
        @PathVariable commentId: Long,
        @RequestParam content: String,
        @AuthorizedUser user: User,
        redirectAttributes: RedirectAttributes,
    ): String {
        if (content.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "답글 내용을 입력해주세요.")
            return Redirect.to(categoryId, figureName)
        }

        try {
            commentService.addReply(commentId, content, user)
            redirectAttributes.addFlashAttribute("success", "답글이 성공적으로 등록되었습니다.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", e.message ?: "답글 등록에 실패했습니다.")
        }

        return Redirect.to(categoryId, figureName)
    }
}
