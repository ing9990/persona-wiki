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
        with(kakaoAuthProperties) {
            require(clientId.isNotBlank()) { "clientId must not be null or blank" }
            require(redirectUri.isNotBlank()) { "redirectUri must not be null or blank" }
        }

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
        with(naverAuthProperties) {
            require(clientId.isNotBlank()) { "clientId must not be null or blank" }
            require(clientSecret.isNotBlank()) { "clientSecret must not be null or blank" }
            require(redirectUri.isNotBlank()) { "redirectUri must not be null or blank" }
        }

        val uri = naverAuthProperties.tokenUri
        val clientId = naverAuthProperties.clientId
        val clientSecret = naverAuthProperties.clientSecret
        val redirectUri = naverAuthProperties.redirectUri
        val state = "STATE_STRING"

        val fullUri: String =
            "https://nid.naver.com/oauth2.0/authorize?" +
                "response_type=code" +
                "&grant_type=authorization_code" +
                "&client_id=xEGDynHBKujCMrZ5M1g1" +
                "&client_secret=wPumA7iScR" +
                "&redirect_uri=http://localhost:8080/auth/naver/callback" +
                "&state=STATE_STRING"

        return fullUri
    }
}
