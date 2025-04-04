package io.ing9990.web.controller

import io.ing9990.domain.user.User
import io.ing9990.web.aop.AuthorizedUser
import io.ing9990.web.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/me")
    fun myPage(
        @AuthorizedUser user: User,
        model: Model,
    ): String {
        model.addAttribute("user", user)
        return "user/my-page"
    }

    @GetMapping("/activity")
    fun activityPage(
        @AuthorizedUser user: User,
        model: Model,
    ): String {
        model.addAttribute("user", user)
        val activities = userService.getUserById(user.id!!)
        model.addAttribute("activities", activities)
        return "user/activity"
    }

    @PostMapping("/me/update-profile")
    fun updateProfile(
        @AuthorizedUser user: User,
        redirectAttributes: RedirectAttributes,
    ): String {
        userService.updateProfileImage(user.id)

        return "redirect:/me"
    }

    @PostMapping("/me/update-nickname")
    fun updateNickname(
        @AuthorizedUser user: User,
        @RequestParam("nickname") nickname: String,
        redirectAttributes: RedirectAttributes,
    ): String {
        try {
            if (nickname.isNotBlank()) {
                userService.updateNickname(user.id!!, nickname)
                redirectAttributes.addFlashAttribute("success", "닉네임이 업데이트되었습니다.")
            } else {
                redirectAttributes.addFlashAttribute("error", "닉네임은 비워둘 수 없습니다.")
            }
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "닉네임 업데이트 중 오류가 발생했습니다: ${e.message}")
        }
        return "redirect:/me"
    }
}
