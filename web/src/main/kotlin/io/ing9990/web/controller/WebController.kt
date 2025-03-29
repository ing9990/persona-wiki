package io.ing9990.web.controller

import io.ing9990.domain.figure.service.CategoryService
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
        // 카테고리와 함께 모든 인물 목록 조회
        val figures = figureService.findAllWithCategory()
        model.addAttribute("figures", figures)

        // 모든 카테고리 조회
        val allCategories = categoryService.getAllCategories()
        model.addAttribute("allCategories", allCategories)

        // 인기 카테고리 (최대 3개) 조회
        // 인기 카테고리는 인물이 많은 순으로 정렬
        val popularCategories =
            allCategories.map { category ->
                val figuresCount = figureService.findByCategoryId(category.id).size
                Pair(category, figuresCount)
            }.sortedByDescending { it.second }
                .take(3)
                .map { it.first }

        model.addAttribute("popularCategories", popularCategories)

        return "index"
    }

    /**
     * 인물 검색 기능
     * 검색 결과 페이지로 이동합니다.
     * @param query 검색어
     */
    @GetMapping("/search")
    fun search(
        @RequestParam query: String,
        model: Model,
    ): String {
        // 검색어가 비어 있는지 확인
        if (query.isBlank()) {
            return "redirect:/"
        }

        // 검색어로 인물 검색 (카테고리 함께 로딩)
        // 일반 검색과 초성 검색 모두 지원
        val searchResults =
            if (query.length <= 3 && query.any { it in 'ㄱ'..'ㅎ' }) {
                figureService.searchByNameWithInitials(query)
            } else {
                figureService.searchByName(query)
            }

        model.addAttribute("searchResults", searchResults)
        model.addAttribute("query", query)

        return "search-results" // 검색 결과 페이지 템플릿 이름
    }
}
