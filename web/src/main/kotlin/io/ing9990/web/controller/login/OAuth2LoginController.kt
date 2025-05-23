package io.ing9990.web.controller.login

import io.ing9990.authentication.OAuthUserProfile
import io.ing9990.authentication.providers.OAuthProviders
import io.ing9990.domain.user.OAuthProviderType
import io.ing9990.domain.user.User
import io.ing9990.service.UserService
import jakarta.servlet.http.HttpSession
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/auth")
class OAuth2LoginController(
    private val oAuthProviders: OAuthProviders,
    private val userService: UserService,
) {
    private val log = LoggerFactory.getLogger(OAuth2LoginController::class.java)

    /**
     * 소셜 로그인 콜백 처리
     * @param providerType 소셜 로그인 제공자 타입(kakao, naver)
     * @param code 인증 코드
     * @param session HTTP 세션
     * @param redirectAttributes 리다이렉트 속성
     * @return 리다이렉트 경로
     */
    @GetMapping("/{providerType}/callback")
    fun oauthCallback(
        @PathVariable providerType: String,
        @RequestParam code: String,
        session: HttpSession,
        redirectAttributes: RedirectAttributes,
    ): String {
        try {
            log.info("$providerType login successful (code=$code)")
            val provider = oAuthProviders.map(providerType.uppercase())
            val profile: OAuthUserProfile = provider.getUserProfile(code)

            val user: User =
                userService.saveOrUpdateUser(
                    profile,
                    OAuthProviderType.valueOf(providerType.uppercase()),
                )

            session.setAttribute("userId", user.id)
            session.setAttribute("user", user)
            session.setAttribute("loggedIn", true)

            redirectAttributes.addFlashAttribute("success", "로그인되었습니다.")
            return "redirect:/"
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "로그인 중 오류가 발생했습니다: ${e.message}")
            return "redirect:/login"
        }
    }

    /**
     * 로그아웃 처리
     * @param session HTTP 세션
     * @param redirectAttributes 리다이렉트 속성
     * @return 리다이렉트 경로
     */
    @GetMapping("/logout")
    fun logout(
        session: HttpSession,
        redirectAttributes: RedirectAttributes,
    ): String {
        session.invalidate()
        redirectAttributes.addFlashAttribute("success", "로그아웃되었습니다.")
        return "redirect:/"
    }
}
