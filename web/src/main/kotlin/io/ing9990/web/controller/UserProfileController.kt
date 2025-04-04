package io.ing9990.web.controller

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
        val user =
            userService.findByNickname(nickname)
                ?: throw IllegalArgumentException("존재하지 않는 사용자입니다.")

        model.addAttribute("profileUser", user)

        // 사용자가 작성한 댓글 목록도 추가할 수 있음
        // model.addAttribute("userComments", commentService.findByUserId(user.id))

        return "user/profile"
    }
}
