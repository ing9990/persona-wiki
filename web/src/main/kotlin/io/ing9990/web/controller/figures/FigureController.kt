package io.ing9990.web.controller.figures

import io.ing9990.aop.AuthorizedUser
import io.ing9990.aop.CurrentUser
import io.ing9990.aop.resolver.CurrentUserDto
import io.ing9990.common.Redirect
import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.comment.service.CommentService
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.figure.service.dto.FigureDetailsResult
import io.ing9990.domain.figure.service.dto.PopularFiguresByCategoriesResult
import io.ing9990.domain.user.User
import io.ing9990.domain.user.repositories.UserRepository
import io.ing9990.domain.vote.service.VoteService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class FigureController(
    private val figureService: FigureService,
    private val categoryService: CategoryService,
    private val voteService: VoteService,
    private val commentService: CommentService,
    private val userRepository: UserRepository,
) {
    /**
     * 인물 목록 페이지 - 카테고리별 인기 인물 표시
     * 인물이 많은 상위 10개 카테고리의 인물을 각각 3명씩 표시
     */
    @GetMapping("/figures")
    fun listFiguresByCategory(model: Model): String {
        val result: PopularFiguresByCategoriesResult =
            categoryService.getPopularFiguresByCategory(3)

        model.addAttribute("categories", result)

        return "figure/figure-list-by-category"
    }

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
     * 인물을 등록합니다.
     */
    @PostMapping("/figures")
    fun addFigure(
        @AuthorizedUser user: User,
        @Valid @ModelAttribute request: CreateFigureRequest,
    ): String {
        val figure: Figure = figureService.createFigure(request.toData(user))

        return Redirect.to(figure.category.id, figure.name)
    }

    /**
     * 인물을 상세조회 합니다.
     */
    @GetMapping("/{categoryId}/@{figureName}")
    fun figureDetail(
        @CurrentUser user: CurrentUserDto,
        @PathVariable categoryId: String,
        @PathVariable figureName: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "15") size: Int,
        model: Model,
    ): String {
        val detailsResult: FigureDetailsResult =
            figureService.findByCategoryIdAndNameWithDetails(
                categoryId = categoryId,
                figureName = figureName,
                userId = user.getUserIdOrDefault(),
                page = page,
                size = size,
            )

        model.addAttribute("detailsResult", detailsResult)

        return "figure/figure-detail"
    }
}
