package io.ing9990.admin.controller

import io.ing9990.admin.dto.CategoryDto
import io.ing9990.admin.service.AdminCategoryService
import io.ing9990.admin.service.AdminFigureService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/persona-admin/categories")
class AdminCategoryController(
    private val categoryService: AdminCategoryService,
    private val figureService: AdminFigureService,
) {
    /**
     * 카테고리 목록 페이지
     */
    @GetMapping
    fun listCategories(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        model: Model,
    ): String {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val categoriesPage = categoryService.getCategoriesPaged(pageable)

        // 카테고리별 인물 수 계산
        val figureCountsByCategory = mutableMapOf<String, Int>()
        categoriesPage.content.forEach { category ->
            val count = figureService.getFiguresByCategoryId(category.id).size
            figureCountsByCategory[category.id] = count
        }

        model.addAttribute("currentPage", "categories")
        model.addAttribute("categoriesPage", categoriesPage)
        model.addAttribute("figureCountsByCategory", figureCountsByCategory)
        model.addAttribute("pageTitle", "카테고리 관리")

        return "category/category-list"
    }

    /**
     * 카테고리 생성 페이지
     */
    @GetMapping("/create")
    fun createCategoryForm(model: Model): String {
        model.addAttribute("categoryDto", CategoryDto())
        model.addAttribute("pageTitle", "카테고리 생성")
        model.addAttribute("isCreating", true)
        model.addAttribute("currentPage", "categories")

        return "category/category-form"
    }

    /**
     * 카테고리 생성 처리
     */
    @PostMapping("/create")
    fun createCategory(
        @Validated @ModelAttribute categoryDto: CategoryDto,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes,
        model: Model,
    ): String {
        // 유효성 검사 실패 시
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "카테고리 생성")
            model.addAttribute("isCreating", true)
            return "category/category-form"
        }

        try {
            categoryService.createCategory(
                id = categoryDto.id,
                displayName = categoryDto.displayName,
                description = categoryDto.description,
                imageUrl = categoryDto.imageUrl,
            )

            redirectAttributes.addFlashAttribute("successMessage", "카테고리가 성공적으로 생성되었습니다.")
            return "redirect:/persona-admin/categories"
        } catch (e: Exception) {
            model.addAttribute("pageTitle", "카테고리 생성")
            model.addAttribute("isCreating", true)
            model.addAttribute("errorMessage", e.message)
            return "category/category-form"
        }
    }

    /**
     * 카테고리 수정 페이지
     */
    @GetMapping("/edit/{id}")
    fun editCategoryForm(
        @PathVariable id: String,
        model: Model,
    ): String {
        val category =
            categoryService.getCategoryById(id)
                ?: return "redirect:/persona-admin/categories"

        val categoryDto =
            CategoryDto(
                id = category.id,
                displayName = category.displayName,
                description = category.description,
                imageUrl = category.imageUrl,
            )

        model.addAttribute("currentPage", "categories")
        model.addAttribute("categoryDto", categoryDto)
        model.addAttribute("pageTitle", "카테고리 수정")
        model.addAttribute("isCreating", false)

        return "category/category-form"
    }

    /**
     * 카테고리 수정 처리
     */
    @PostMapping("/edit/{id}")
    fun editCategory(
        @PathVariable id: String,
        @Validated @ModelAttribute categoryDto: CategoryDto,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes,
        model: Model,
    ): String {
        // 유효성 검사 실패 시
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "카테고리 수정")
            model.addAttribute("isCreating", false)
            return "category/category-form"
        }

        try {
            categoryService.updateCategory(
                id = id,
                displayName = categoryDto.displayName,
                description = categoryDto.description,
                imageUrl = categoryDto.imageUrl,
            )

            redirectAttributes.addFlashAttribute("successMessage", "카테고리가 성공적으로 수정되었습니다.")
            return "redirect:/persona-admin/categories"
        } catch (e: Exception) {
            model.addAttribute("pageTitle", "카테고리 수정")
            model.addAttribute("isCreating", false)
            model.addAttribute("errorMessage", e.message)
            return "category/category-form"
        }
    }

    /**
     * 카테고리 삭제
     */
    @PostMapping("/delete/{id}")
    fun deleteCategory(
        @PathVariable id: String,
        redirectAttributes: RedirectAttributes,
    ): String {
        try {
            categoryService.deleteCategory(id)
            redirectAttributes.addFlashAttribute("successMessage", "카테고리가 성공적으로 삭제되었습니다.")
        } catch (e: IllegalStateException) {
            redirectAttributes.addFlashAttribute("errorMessage", e.message)
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "카테고리 삭제 중 오류가 발생했습니다: ${e.message}",
            )
        }

        return "redirect:/persona-admin/categories"
    }
}
