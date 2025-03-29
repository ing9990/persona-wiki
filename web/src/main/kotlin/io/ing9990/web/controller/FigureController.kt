package io.ing9990.web.controller

import io.ing9990.domain.figure.Sentiment
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.web.exceptions.EntityNotFoundException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 인물 관련 페이지를 처리하는 컨트롤러
 */
@Controller
class FigureController(
    private val figureService: FigureService
) {

    /**
     * 인물 상세 페이지를 렌더링합니다.
     * 새로운 URL 형식: /{categoryId}/@{figureName}
     * 인물이 없는 경우 EntityNotFoundException 발생
     */
    @GetMapping("/{categoryId}/@{figureName}")
    fun figureDetail(
        @PathVariable categoryId: String,
        @PathVariable figureName: String,
        model: Model
    ): String {
        // 카테고리 정보 조회
        val category = figureService.findCategoryById(categoryId)

        // 인물 정보 조회 (카테고리와 함께 로딩)
        val figure = figureService.findByCategoryIdAndNameWithDetails(categoryId, figureName)
            ?: throw EntityNotFoundException("Figure", "$categoryId/$figureName")

        // 인물의 댓글 목록을 별도로 조회 (페이지네이션 지원을 위한 준비)
        val comments = figureService.findCommentsByFigureId(figure.id!!)

        model.addAttribute("category", category)
        model.addAttribute("figure", figure)
        model.addAttribute("comments", comments)

        return "figure/figure-detail"
    }

    /**
     * 인물에 대한 새 댓글을 추가합니다.
     */
    @PostMapping("/{categoryId}/@{figureName}/comment")
    fun addComment(
        @PathVariable categoryId: String,
        @PathVariable figureName: String,
        @RequestParam content: String,
        redirectAttributes: RedirectAttributes
    ): String {
        // 내용이 비어있는지 확인
        if (content.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "댓글 내용을 입력해주세요.")
            return getRedirectUrl(categoryId, figureName)
        }

        // 인물 찾기
        val figure = figureService.findByCategoryIdAndNameWithDetails(categoryId, figureName)
            ?: throw EntityNotFoundException("Figure", "$categoryId/$figureName")

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
        redirectAttributes: RedirectAttributes
    ): String {
        // 인물 찾기
        val figure = figureService.findByCategoryIdAndNameWithDetails(categoryId, figureName)
            ?: throw EntityNotFoundException("Figure", "$categoryId/$figureName")

        // 감정 분석
        val sentimentEnum = when (sentiment.uppercase()) {
            "POSITIVE" -> Sentiment.POSITIVE
            "NEGATIVE" -> Sentiment.NEGATIVE
            else -> Sentiment.NEUTRAL
        }

        // 투표 등록
        figureService.voteFigure(figure.id!!, sentimentEnum)

        // 성공 메시지
        val message = when (sentimentEnum) {
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
        @PathVariable figureName: String
    ): String {
        return getRedirectUrl(categoryId, figureName)
    }

    /**
     * URL 인코딩을 적용한 리다이렉트 URL을 생성합니다.
     */
    private fun getRedirectUrl(categoryId: String, figureName: String): String {
        val encodedFigureName = URLEncoder.encode(figureName, StandardCharsets.UTF_8.name())
        return "redirect:/$categoryId/@$encodedFigureName"
    }
}