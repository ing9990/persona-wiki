package io.ing9990.web.controller.users

import io.ing9990.domain.activities.repositories.querydsl.dto.ActivityOverviewResult
import io.ing9990.domain.activities.repositories.querydsl.dto.RecentActivityResult
import io.ing9990.domain.activities.service.ActivityService
import io.ing9990.domain.user.User
import io.ing9990.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/users")
class UserProfileController(
    private val userService: UserService,
    private val activityService: ActivityService,
) {
    /**
     * 사용자 프로필 페이지
     */
    @GetMapping("/{nickname}")
    fun userProfile(
        @PathVariable nickname: String,
        model: Model,
    ): String {
        // 닉네임으로 사용자 조회
        val profile: User =
            userService.findByNickname(nickname)
                ?: throw IllegalArgumentException("존재하지 않는 사용자입니다.")

        val recentActivities:
            List<RecentActivityResult> =
            activityService.getRecentActivities(
                profile.id!!,
                5,
            )
        val activityOverview: ActivityOverviewResult =
            activityService.getActivityOverview(profile.id!!)

        model.addAttribute(
            "recentActivities",
            recentActivities,
        )
        model.addAttribute(
            "activityOverview",
            activityOverview,
        )
        model.addAttribute("profile", profile)

        return "user/profile"
    }
}
