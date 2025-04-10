package io.ing9990.web.controller

import io.ing9990.aop.CurrentUser
import io.ing9990.aop.resolver.CurrentUserDto
import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.figure.service.dto.FigureCardResult
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * 메인 페이지와 검색 기능을 처리하는 컨트롤러
 */
@Controller
class WebController(
    private val figureService: FigureService,
    private val categoryService: CategoryService,
) {
    /**
     * 메인 페이지입니다.
     *
     * 주목받는 인물들
     * 투표 수와 댓글 수를 합친 값을 내림차 순으로 보여줍니다.
     */
    @GetMapping("/")
    fun index(
        @CurrentUser currentUser: CurrentUserDto,
        model: Model,
    ): String {
        val popularFigures: List<FigureCardResult> = figureService.getPopularFigures(3 * 30 - 1)

        model.addAttribute("figures", popularFigures)

        return "index"
    }

    @GetMapping("/search")
    fun search(
        @RequestParam query: String,
        model: Model,
    ): String {
        val searchResults: List<FigureCardResult> =
            figureService.searchByName(query)

        model.addAttribute("searchResults", searchResults)
        model.addAttribute("query", query)

        return "search/search-results"
    }

    @GetMapping("/privacy")
    fun privacy(model: Model): String = "privacy/privacy"

    @GetMapping("/terms")
    fun terms(model: Model): String = "privacy/terms"
}
