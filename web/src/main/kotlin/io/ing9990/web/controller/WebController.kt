package io.ing9990.web.controller

import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.figure.service.FigureService
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
     * 메인 페이지를 렌더링합니다.
     * 카테고리별 인물 목록과 인기 카테고리를 표시합니다.
     */
    @GetMapping("/")
    fun index(model: Model): String {
        // 인기 인물 6명 조회 (평판투표 + 댓글 수 기준)
        val popularFigures = figureService.getPopularFigures(6)
        model.addAttribute("figures", popularFigures)

        // 모든 카테고리 조회
        val allCategories = categoryService.getAllCategories()
        model.addAttribute("allCategories", allCategories)

        return "index"
    }

    @GetMapping("/search")
    fun search(
        @RequestParam query: String,
        model: Model,
    ): String {
        // 검색어가 비어 있는지 확인
        if (query.isBlank()) {
            throw IllegalArgumentException("검색어를 입력해주세요")
        }

        // 검색어로 인물 검색 (이제 서비스에서 예외를 발생시키므로 try-catch 불필요)
        val searchResults =
            if (query.length <= 3 && query.any { it in 'ㄱ'..'ㅎ' }) {
                figureService.searchByNameWithInitials(query)
            } else {
                figureService.searchByName(query)
            }

        model.addAttribute("searchResults", searchResults)
        model.addAttribute("query", query)

        return "search/search-results"
    }

    @GetMapping("/privacy")
    fun privacy(model: Model): String = "privacy/privacy"

    @GetMapping("/terms")
    fun terms(model: Model): String = "privacy/terms"
}
