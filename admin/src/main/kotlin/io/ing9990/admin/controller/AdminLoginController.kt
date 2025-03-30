package io.ing9990.admin.controller

import io.ing9990.admin.dto.FigureListDto
import io.ing9990.admin.service.AdminCategoryService
import io.ing9990.admin.service.AdminCommentService
import io.ing9990.admin.service.AdminFigureService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/persona-admin")
class AdminLoginController(
    private val categoryService: AdminCategoryService,
    private val figureService: AdminFigureService,
    private val commentService: AdminCommentService,
) {
    @GetMapping("/login")
    fun loginPage(model: Model): String {
        model.addAttribute("currentPage", "login")
        return "login"
    }

    @GetMapping
    fun dashboard(model: Model): String {
        // 통계 정보 조회
        val categoryCount = categoryService.getCategoryCount()
        val figureCount = figureService.getFigureCount()
        val commentCount = commentService.getCommentCount()

        // 최근 추가된 인물 조회 및 DTO 변환
        val recentFigures = figureService.getRecentFigures(5)
        val recentFiguresDto =
            recentFigures.map { figure ->
                FigureListDto.from(figure)
            }

        // 모델에 데이터 추가
        model.addAttribute("categoryCount", categoryCount)
        model.addAttribute("figureCount", figureCount)
        model.addAttribute("commentCount", commentCount)
        model.addAttribute("recentFigures", recentFiguresDto)
        model.addAttribute("pageTitle", "대시보드")
        model.addAttribute("currentPage", "dashboard")

        return "dashboard"
    }
}
