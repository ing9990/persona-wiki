package io.ing9990.web.controller.figures

import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.category.service.dto.CategoryIds
import io.ing9990.domain.category.service.dto.CategoryResult
import io.ing9990.domain.figure.service.FigureService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/categories")
class CategoryController(
    private val categoryService: CategoryService,
    private val figureService: FigureService,
) {
    /**
     * 카테고리 목록 페이지
     * 각 카테고리별 인물 수를 함께 표시
     * 페이지네이션 추가
     */
    @GetMapping
    fun listCategories(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "9") size: Int,
    ): String {
        // 모든 카테고리 조회 (페이지네이션용)
        val allCategories: List<CategoryResult> =
            categoryService.getAllCategories()

        // 페이지네이션 계산
        val totalCategories = allCategories.size
        val totalPages = (totalCategories + size - 1) / size // 올림 나눗셈 계산

        // 현재 페이지에 표시할 카테고리 추출
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, totalCategories)
        val categories =
            if (startIndex < totalCategories) {
                allCategories.subList(startIndex, endIndex)
            } else {
                emptyList()
            }

        // 카테고리별 인물 수 조회를 위한 맵 생성
        val figuresByCategory = mutableMapOf<String, List<Any>>()

        // 각 카테고리별 인물 목록 조회
        categories.forEach { category ->
            val figures = figureService.findByCategoryId(category.id)
            figuresByCategory[category.id] = figures
        }

        model.addAttribute("categories", categories)
        model.addAttribute("figuresByCategory", figuresByCategory)
        model.addAttribute("currentPage", page)
        model.addAttribute("totalPages", totalPages)
        model.addAttribute("totalCategories", totalCategories)

        return "category/category-list"
    }

    @GetMapping("/{categoryId}")
    fun getCategoryDetails(
        @PathVariable categoryId: String,
        model: Model,
    ): String {
        val category = categoryService.getCategoryById(categoryId)
        val figures = figureService.findByCategoryId(categoryId)

        // 관련 카테고리 목록 조회 (현재 카테고리를 제외한 다른 카테고리들)
        val allCategories = categoryService.getAllCategories()
        val relatedCategories =
            categoryService.getAllCategoriesWithNotIn(
                CategoryIds(mutableListOf(categoryId)),
                4,
            )

        allCategories.filter { it.id != categoryId }.take(4)

        model.addAttribute("category", category)
        model.addAttribute("figures", figures)
        model.addAttribute("relatedCategories", relatedCategories)

        return "category/category-detail"
    }

    // 카테고리 추가 페이지를 렌더링하는 메서드 추가
    @GetMapping("/add")
    fun addCategoryForm(): String = "category/add-category"
}
