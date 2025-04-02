package io.ing9990.web.controller

import io.ing9990.authentication.providers.kakao.KakaoAuthProperties
import io.ing9990.authentication.providers.naver.NaverAuthProperties
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController(
    private val kakaoAuthProperties: KakaoAuthProperties,
    private val naverAuthProperties: NaverAuthProperties,
) {
    @GetMapping("/login")
    fun loginPage(model: Model): String {
        // 소셜 로그인 URL 설정
        model.addAttribute("kakaoAuthUrl", getKakaoAuthUrl())
        model.addAttribute("naverAuthUrl", getNaverAuthUrl())

        return "login/login"
    }

    /**
     * 카카오 인증 URL 생성
     */
    private fun getKakaoAuthUrl(): String {
        val clientId = kakaoAuthProperties.clientId
        val redirectUri = kakaoAuthProperties.redirectUri

        return "https://kauth.kakao.com/oauth/authorize" +
            "?client_id=$clientId" +
            "&redirect_uri=$redirectUri" +
            "&response_type=code"
    }

    /**
     * 네이버 인증 URL 생성
     */
    private fun getNaverAuthUrl(): String {
        val clientId = naverAuthProperties.clientId
        val redirectUri = naverAuthProperties.redirectUri
        val state = "STATE_STRING"

        return "https://nid.naver.com/oauth2.0/authorize" +
            "?client_id=$clientId" +
            "&redirect_uri=$redirectUri" +
            "&response_type=code" +
            "&state=$state"
    }
}
