package io.ing9990.web.controller.users

import io.ing9990.aop.AuthorizedUser
import io.ing9990.domain.activities.repositories.querydsl.dto.ActivityOverviewResult
import io.ing9990.domain.activities.repositories.querydsl.dto.RecentActivityResult
import io.ing9990.domain.activities.service.ActivityService
import io.ing9990.domain.user.User
import io.ing9990.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class UserController(
    private val userService: UserService,
    private val activityService: ActivityService,
) {
    @GetMapping("/me")
    fun myPage(
        @AuthorizedUser user: User,
        model: Model,
    ): String {
        val recentActivitiesLimit: Int = 5

        val recentActivities: List<RecentActivityResult> =
            activityService.getRecentActivities(
                user.id!!,
                recentActivitiesLimit,
            )

        val activityOverviewResult: ActivityOverviewResult =
            activityService.getActivityOverview(user.id!!)

        model.addAttribute(
            "recentActivities",
            recentActivities,
        )

        model.addAttribute(
            "activityOverview",
            activityOverviewResult,
        )
        model.addAttribute("user", user)

        return "user/my-page"
    }

    @GetMapping("/activity")
    fun activityPage(
        @AuthorizedUser user: User,
        @PageableDefault(size = 20) pageable: Pageable,
        @RequestParam(required = false) type: String?,
        model: Model,
    ): String {
        model.addAttribute("user", user)

        // 타입별 필터링 지원
        val activities: Page<RecentActivityResult> =
            if (type != null) {
                activityService.getActivitiesByTypeAndUserId(user.id!!, type, pageable)
            } else {
                activityService.getAllActivitiesByUserId(user.id!!, pageable)
            }

        model.addAttribute("activities", activities)
        model.addAttribute("activityTypes", activityService.getActivityTypes())
        model.addAttribute("selectedType", type)

        // 활동 요약 정보도 함께 제공
        model.addAttribute(
            "activityOverview",
            activityService.getActivityOverview(user.id!!),
        )

        return "user/activity"
    }

    @PostMapping("/me/delete-profile")
    fun deleteProfile(
        @AuthorizedUser user: User,
    ): String {
        userService.removeProfileImage(user.id!!)

        return "redirect:/me"
    }

    @PostMapping("/me/update-bio")
    fun updateBio(
        @AuthorizedUser user: User,
        @RequestParam("bio") bio: String,
        redirectAttributes: RedirectAttributes,
    ): String {
        userService.updateBio(user.id!!, bio)
        redirectAttributes.addFlashAttribute("message", "한줄 소개가 업데이트되었습니다.")
        return "redirect:/me"
    }

    @PostMapping("/me/update-nickname")
    fun updateNickname(
        @AuthorizedUser user: User,
        @RequestParam("nickname") nickname: String,
        redirectAttributes: RedirectAttributes,
    ): String {
        try {
            userService.updateNickname(user.id!!, nickname)
            redirectAttributes.addFlashAttribute("success", "닉네임이 업데이트되었습니다.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", e.message)
            return "redirect:/me"
        }

        return "redirect:/me"
    }

    // PATCH 메서드도 지원 (API 요청용)
    @PatchMapping("/me/update-bio")
    @ResponseBody
    fun updateBioApi(
        @AuthorizedUser user: User,
        @RequestBody request: UpdateBioRequest,
    ): UpdateBioResponse {
        userService.updateBio(user.id!!, request.bio)
        return UpdateBioResponse(success = true, message = "한줄 소개가 업데이트되었습니다.")
    }
}
