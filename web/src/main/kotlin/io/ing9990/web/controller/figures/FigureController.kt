package io.ing9990.web.controller.figures

import io.ing9990.aop.AuthorizedUser
import io.ing9990.aop.CurrentUser
import io.ing9990.aop.resolver.CurrentUserDto
import io.ing9990.common.Redirect
import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.figure.service.dto.FigureDetailsResult
import io.ing9990.domain.figure.service.dto.PopularFiguresByCategoriesResult
import io.ing9990.domain.user.User
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
) {
    /**
     * 인물 목록 페이지 - 카테고리별 인기 인물 표시
     * 인물이 많은 상위 10개 카테고리의 인물을 각각 3명씩 표시
     */
    @GetMapping("/figures")
    fun listFiguresByCategory(model: Model): String {
        val result: PopularFiguresByCategoriesResult =
            categoryService.getPopularFiguresByCategory(6)

        model.addAttribute("categoryResult", result)

        return "figure/figure-list-by-category"
    }

    /**
     * 인물을 등록합니다 (폼 제출용)
     */
    @PostMapping("/figures")
    fun addFigure(
        @AuthorizedUser user: User,
        model: Model,
        @Valid @ModelAttribute request: CreateFigureRequest,
    ): String {
        val figure: Figure = figureService.createFigure(request.toData(user))

        return Redirect.to(figure.category.id, figure.slug)
    }

    /**
     * 인물을 상세조회 합니다.
     */
    @GetMapping("/{categoryId}/@{slug}")
    fun figureDetail(
        @CurrentUser user: CurrentUserDto,
        @PathVariable categoryId: String,
        @PathVariable slug: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "15") size: Int,
        model: Model,
    ): String {
        val detailsResult: FigureDetailsResult =
            figureService.findByCategoryIdAndNameWithDetails(
                categoryId = categoryId,
                slug = slug,
                userId = user.getUserIdOrDefault(),
                page = page,
                size = size,
            )

        model.addAttribute("detailsResult", detailsResult)

        return "figure/figure-detail"
    }

    @GetMapping("/figures/{categoryId}/{slug}")
    fun redirectFromLegacyUrl(
        @PathVariable categoryId: String,
        @PathVariable slug: String,
    ): String = Redirect.to(categoryId, slug)
}
