package io.ing9990.web.controller

import io.ing9990.domain.figure.Comment
import io.ing9990.domain.figure.Sentiment
import io.ing9990.domain.figure.service.CategoryService
import io.ing9990.domain.figure.service.FigureService
import org.springframework.data.domain.Page
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
        // 카테고리별 인기 인물 조회 (각 카테고리별 3명씩)
        val figuresByCategory = figureService.getPopularFiguresByCategory(3)

        model.addAttribute("categoriesWithFigures", figuresByCategory)

        return "figure/figure-list-by-category"
    }

    /**
     * 인물에 대한 새 댓글을 추가합니다.
     */
    @PostMapping("/{categoryId}/@{figureName}/comment")
    fun addComment(
        @PathVariable categoryId: String,
        @PathVariable figureName: String,
        @RequestParam content: String,
        redirectAttributes: RedirectAttributes,
    ): String {
        // 내용이 비어있는지 확인
        if (content.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "댓글 내용을 입력해주세요.")
            return getRedirectUrl(categoryId, figureName)
        }

        // 인물 찾기
        val figure =
            figureService.findByCategoryIdAndNameWithDetails(categoryId, figureName)

        // 댓글 추가
        figureService.addComment(figure.id!!, content)

        // 성공 메시지
        redirectAttributes.addFlashAttribute("success", "댓글이 성공적으로 등록되었습니다.")

        return getRedirectUrl(categoryId, figureName)
    }

    /**
     * 인물에 대한 평가(숭배/중립/사형)를 등록합니다.
     */
    @PostMapping("/{categoryId}/@{figureName}/vote")
    fun voteForFigure(
        @PathVariable categoryId: String,
        @PathVariable figureName: String,
        @RequestParam sentiment: String,
        redirectAttributes: RedirectAttributes,
    ): String {
        // 인물 찾기
        val figure =
            figureService.findByCategoryIdAndNameWithDetails(categoryId, figureName)

        // 감정 분석
        val sentimentEnum =
            when (sentiment.uppercase()) {
                "POSITIVE" -> Sentiment.POSITIVE
                "NEGATIVE" -> Sentiment.NEGATIVE
                else -> Sentiment.NEUTRAL
            }

        // 투표 등록
        figureService.voteFigure(figure.id!!, sentimentEnum)

        // 성공 메시지
        val message =
            when (sentimentEnum) {
                Sentiment.POSITIVE -> "숭배 평가가 등록되었습니다."
                Sentiment.NEGATIVE -> "사형 평가가 등록되었습니다."
                Sentiment.NEUTRAL -> "중립 평가가 등록되었습니다."
            }
        redirectAttributes.addFlashAttribute("success", message)

        return getRedirectUrl(categoryId, figureName)
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
    ): String {
        return getRedirectUrl(categoryId, figureName)
    }

    /**
     * URL 인코딩을 적용한 리다이렉트 URL을 생성합니다.
     */
    private fun getRedirectUrl(
        categoryId: String,
        figureName: String,
    ): String {
        val encodedFigureName = URLEncoder.encode(figureName, StandardCharsets.UTF_8.name())
        return "redirect:/$categoryId/@$encodedFigureName"
    }

    /**
     * 인물 추가 페이지를 렌더링합니다.
     * 검색 결과에서 넘어온 경우 name 파라미터로 검색어를 받아서 자동으로 채워줍니다.
     */
    @GetMapping("/add-figure")
    fun addFigureForm(
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) name: String?,
        model: Model,
    ): String {
        // 카테고리 목록을 모델에 추가
        val categories = categoryService.getAllCategories()
        model.addAttribute("categories", categories)

        // 쿼리 파라미터로 카테고리 ID가 전달된 경우, 해당 카테고리를 선택된 상태로 설정
        if (category != null) {
            model.addAttribute("selectedCategoryId", category)
        }

        // 쿼리 파라미터로 이름이 전달된 경우, 해당 이름을 자동으로 채워줌
        if (name != null) {
            model.addAttribute("figureName", name)
        }

        return "figure/add-figure"
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
        redirectAttributes: RedirectAttributes,
    ): String {
        // 내용이 비어있는지 확인
        if (content.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "답글 내용을 입력해주세요.")
            return getRedirectUrl(categoryId, figureName)
        }

        try {
            // 답글 추가
            figureService.addReply(commentId, content)

            // 성공 메시지
            redirectAttributes.addFlashAttribute("success", "답글이 성공적으로 등록되었습니다.")
        } catch (e: Exception) {
            // 오류 메시지
            redirectAttributes.addFlashAttribute("error", e.message ?: "답글 등록에 실패했습니다.")
        }

        return getRedirectUrl(categoryId, figureName)
    }

    /**
     * 인물 상세 페이지에서 댓글 트리를 표시하기 위해 기존 figureDetail 메서드를 수정
     */
    @GetMapping("/{categoryId}/@{figureName}")
    fun figureDetail(
        @PathVariable categoryId: String,
        @PathVariable figureName: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        model: Model,
    ): String {
        // 카테고리 정보 조회
        val category = figureService.findCategoryById(categoryId)
        val figure = figureService.findByCategoryIdAndNameWithDetails(categoryId, figureName)

        // 인물의 댓글 트리를 페이징하여 조회
        val commentPage: Page<Comment> = figureService.getCommentTreesByFigureId(figure.id!!, page, size)

        model.addAttribute("category", category)
        model.addAttribute("figure", figure)
        model.addAttribute("commentPage", commentPage)

        return "figure/figure-detail"
    }
}
