package io.ing9990.admin.controller

import io.ing9990.admin.dto.FigureDto
import io.ing9990.admin.dto.FigureListDto
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
@RequestMapping("/persona-admin/figures")
class AdminFigureController(
    private val figureService: AdminFigureService,
    private val categoryService: AdminCategoryService,
) {
    @GetMapping
    fun listFigures(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) search: String?,
        model: Model,
    ): String {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val figuresPage = figureService.getFiguresPaged(pageable)

        // 카테고리 목록 가져오기
        val categories = categoryService.getAllCategories()

        val figuresDto =
            figuresPage.map { figure ->
                FigureListDto.from(figure)
            }

        model.addAttribute("figuresPage", figuresDto)
        model.addAttribute("categories", categories)
        model.addAttribute("selectedCategory", category)
        model.addAttribute("searchKeyword", search)
        model.addAttribute("pageTitle", "인물 관리")
        model.addAttribute("currentPage", "figures")

        return "figure/figure-list"
    }

    /**
     * 인물 생성 페이지
     */
    @GetMapping("/create")
    fun createFigureForm(model: Model): String {
        // 카테고리 목록 가져오기
        val categories = categoryService.getAllCategories()

        model.addAttribute("figureDto", FigureDto())
        model.addAttribute("categories", categories)
        model.addAttribute("pageTitle", "인물 생성")
        model.addAttribute("isCreating", true)
        model.addAttribute("currentPage", "figures")
        return "figure/figure-form"
    }

    /**
     * 인물 생성 처리
     */
    @PostMapping("/create")
    fun createFigure(
        @Validated @ModelAttribute figureDto: FigureDto,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes,
        model: Model,
    ): String {
        // 유효성 검사 실패 시
        if (bindingResult.hasErrors()) {
            // 카테고리 목록 다시 가져오기
            val categories = categoryService.getAllCategories()
            model.addAttribute("categories", categories)
            model.addAttribute("pageTitle", "인물 생성")
            model.addAttribute("isCreating", true)
            return "figure/figure-form"
        }

        try {
            figureService.createFigure(
                name = figureDto.name,
                categoryId = figureDto.categoryId,
                imageUrl = figureDto.imageUrl,
                bio = figureDto.bio,
            )

            redirectAttributes.addFlashAttribute("successMessage", "인물이 성공적으로 생성되었습니다.")
            return "redirect:/persona-admin/figures"
        } catch (e: Exception) {
            // 카테고리 목록 다시 가져오기
            val categories = categoryService.getAllCategories()
            model.addAttribute("categories", categories)
            model.addAttribute("pageTitle", "인물 생성")
            model.addAttribute("isCreating", true)
            model.addAttribute("errorMessage", e.message)
            model.addAttribute("currentPage", "figures")
            return "figure/figure-form"
        }
    }

    /**
     * 인물 수정 페이지
     */
    @GetMapping("/edit/{id}")
    fun editFigureForm(
        @PathVariable id: Long,
        model: Model,
    ): String {
        val figure =
            figureService.getFigureById(id)
                ?: return "redirect:/persona-admin/figures"

        val figureDto =
            FigureDto(
                id = figure.id,
                name = figure.name,
                categoryId = figure.category.id,
                imageUrl = figure.imageUrl,
                bio = figure.bio,
            )

        // 카테고리 목록 가져오기
        val categories = categoryService.getAllCategories()

        model.addAttribute("figureDto", figureDto)
        model.addAttribute("categories", categories)
        model.addAttribute("pageTitle", "인물 수정")
        model.addAttribute("isCreating", false)
        model.addAttribute("currentPage", "figures")
        return "figure/figure-form"
    }

    /**
     * 인물 수정 처리
     */
    @PostMapping("/edit/{id}")
    fun editFigure(
        @PathVariable id: Long,
        @Validated @ModelAttribute figureDto: FigureDto,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes,
        model: Model,
    ): String {
        // 유효성 검사 실패 시
        if (bindingResult.hasErrors()) {
            // 카테고리 목록 다시 가져오기
            val categories = categoryService.getAllCategories()

            model.addAttribute("categories", categories)
            model.addAttribute("pageTitle", "인물 수정")
            model.addAttribute("isCreating", false)
            model.addAttribute("currentPage", "figures")
            return "figure/figure-form"
        }

        try {
            figureService.updateFigure(
                id = id,
                name = figureDto.name,
                categoryId = figureDto.categoryId,
                imageUrl = figureDto.imageUrl,
                bio = figureDto.bio,
            )

            redirectAttributes.addFlashAttribute("successMessage", "인물이 성공적으로 수정되었습니다.")
            return "redirect:/persona-admin/figures"
        } catch (e: Exception) {
            // 카테고리 목록 다시 가져오기
            val categories = categoryService.getAllCategories()
            model.addAttribute("categories", categories)
            model.addAttribute("pageTitle", "인물 수정")
            model.addAttribute("isCreating", false)
            model.addAttribute("errorMessage", e.message)

            model.addAttribute("currentPage", "figures")
            return "figure/figure-form"
        }
    }

    /**
     * 인물 삭제
     */
    @PostMapping("/delete/{id}")
    fun deleteFigure(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ): String {
        try {
            figureService.deleteFigure(id)
            redirectAttributes.addFlashAttribute("successMessage", "인물이 성공적으로 삭제되었습니다.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "인물 삭제 중 오류가 발생했습니다: ${e.message}")
        }

        return "redirect:/persona-admin/figures"
    }
}
