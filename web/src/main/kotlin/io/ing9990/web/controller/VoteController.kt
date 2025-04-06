package io.ing9990.web.controller

import io.ing9990.aop.AuthorizedUser
import io.ing9990.common.Redirect
import io.ing9990.domain.figure.Sentiment.NEGATIVE
import io.ing9990.domain.figure.Sentiment.NEUTRAL
import io.ing9990.domain.figure.Sentiment.POSITIVE
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.user.User
import io.ing9990.domain.vote.service.VoteService
import io.ing9990.exceptions.UnauthorizedException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class VoteController(
    private val figureService: FigureService,
    private val voteService: VoteService,
) {
    @PostMapping("/{categoryId}/@{figureName}/vote")
    fun voteForFigure(
        @PathVariable categoryId: String,
        @PathVariable figureName: String,
        @RequestParam sentiment: String,
        @AuthorizedUser user: User,
        redirectAttributes: RedirectAttributes,
    ): String {
        try {
            // 인물 찾기
            val figure = figureService.findByCategoryIdAndNameWithDetails(categoryId, figureName)

            // 감정 분석
            val sentimentEnum =
                when (sentiment.uppercase()) {
                    "POSITIVE" -> POSITIVE
                    "NEGATIVE" -> NEGATIVE
                    else -> NEUTRAL
                }

            // 투표 등록 (사용자 정보 포함)
            voteService.voteFigure(figure.id!!, sentimentEnum, user)

            // 성공 메시지
            val message =
                when (sentimentEnum) {
                    POSITIVE -> "숭배 평가가 등록되었습니다."
                    NEGATIVE -> "사형 평가가 등록되었습니다."
                    NEUTRAL -> "중립 평가가 등록되었습니다."
                }
            redirectAttributes.addFlashAttribute("success", message)
        } catch (e: UnauthorizedException) {
            redirectAttributes.addFlashAttribute("error", "투표를 하려면 로그인이 필요합니다.")
            return "redirect:/login"
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", e.message)
        }

        return Redirect.to(categoryId, figureName)
    }
}
