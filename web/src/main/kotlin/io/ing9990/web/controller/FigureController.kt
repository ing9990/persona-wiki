package io.ing9990.web.controller

import io.ing9990.aop.AuthorizedUser
import io.ing9990.aop.CurrentUser
import io.ing9990.common.Redirect
import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.comment.Comment
import io.ing9990.domain.comment.service.CommentService
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.user.User
import io.ing9990.domain.vote.service.VoteService
import org.springframework.data.domain.Page
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller
class FigureController(
    private val figureService: FigureService,
    private val categoryService: CategoryService,
    private val voteService: VoteService,
    private val commentService: CommentService,
) {
    /**
     * 인물 목록 페이지 - 카테고리별 인기 인물 표시
     * 인물이 많은 상위 10개 카테고리의 인물을 각각 3명씩 표시
     */
    @GetMapping("/figures")
    fun listFiguresByCategory(model: Model): String {
        // 카테고리별 인기 인물 조회 (각 카테고리별 3명씩)
        val figuresByCategory = figureService.getPopularFiguresByCategory(3)

        model.addAttribute("categoriesWithFigures", figuresByCategory)

        return "figure/figure-list-by-category"
    }

    /**
     * 이전 URL 형식에서 새로운 형식으로 리다이렉트합니다.
     * 이전: /figures/{categoryId}/{figureName}
     * 새로운: /{categoryId}/@{figureName}
     */
    @GetMapping("/figures/{categoryId}/{figureName}")
    fun redirectFromLegacyUrl(
        @PathVariable categoryId: String,
        @PathVariable figureName: String,
    ): String = Redirect.to(categoryId, figureName)

    @GetMapping("/add-figure")
    fun addFigureForm(
        @AuthorizedUser user: User,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) name: String?,
        model: Model,
    ): String {
        categoryService.getAllCategories().also {
            model.addAttribute("categories", it)
        }
        category.let {
            model.addAttribute("selectedCategoryId", it)
        }
        name.let {
            model.addAttribute("selectedFigureName", it)
        }
        return "figure/add-figure"
    }

    /**
     * 인물을 상세조회 합니다.
     */
    @GetMapping("/{categoryId}/@{figureName}")
    fun figureDetail(
        @CurrentUser user: User?,
        @PathVariable categoryId: String,
        @PathVariable figureName: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        model: Model,
    ): String {
        val category = categoryService.findCategoryById(categoryId)
        val figure = figureService.findByCategoryIdAndNameWithDetails(categoryId, figureName)

        val hasVoted = voteService.hasUserVoted(figure.id!!, user?.id)
        val userVote = figureService.getUserVote(figure.id!!, user?.id)

        val commentPage: Page<Comment> =
            commentService.getCommentTreesByFigureId(figure.id!!, page, size)

        model.addAttribute("category", category)
        model.addAttribute("figure", figure)
        model.addAttribute("commentPage", commentPage)
        model.addAttribute("hasVoted", hasVoted)
        model.addAttribute("userVote", userVote)

        return "figure/figure-detail"
    }
}
