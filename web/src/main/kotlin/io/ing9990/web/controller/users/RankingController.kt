package io.ing9990.web.controller.users

import io.ing9990.domain.user.service.RankingService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * 사용자 랭킹 페이지 컨트롤러
 * CurrentUserArgumentResolver에서 자동으로 currentUserRank 속성이 모델에 추가됩니다.
 */
@Controller
class RankingController(
    private val rankingService: RankingService,
) {
    companion object {
        private const val DEFAULT_SIZE = 20
        private const val MAX_SIZE = 50
    }

    @GetMapping("/ranking")
    fun rankingPage(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "$DEFAULT_SIZE") size: Int,
        model: Model,
    ): String {
        // 최대 크기 제한
        val pageSize = if (size > MAX_SIZE) MAX_SIZE else size

        // 랭킹 데이터 한번에 조회 (통합된 객체로 반환)
        val rankingData =
            rankingService.rankingTopLimit(
                limit = 100,
                page = page,
                size = pageSize,
            )

        // 모델에 통합 데이터 추가
        model.addAttribute("rankingData", rankingData)

        model.addAttribute("currentPage", rankingData.currentPage)
        model.addAttribute("pageSize", rankingData.pageSize)
        model.addAttribute("totalPages", rankingData.totalPages)
        model.addAttribute("totalUsers", rankingData.totalUsers)

        return "user/ranking"
    }
}
