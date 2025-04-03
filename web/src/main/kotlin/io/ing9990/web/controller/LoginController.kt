package io.ing9990.web.controller

import io.ing9990.authentication.providers.google.dto.GoogleAuthProperties
import io.ing9990.authentication.providers.kakao.dto.KakaoAuthProperties
import io.ing9990.authentication.providers.naver.dto.NaverAuthProperties
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController(
    private val kakaoAuthProperties: KakaoAuthProperties,
    private val naverAuthProperties: NaverAuthProperties,
    private val googleAuthProperties: GoogleAuthProperties,
) {
    @GetMapping("/login")
    fun loginPage(model: Model): String {
        // 소셜 로그인 URL 설정
        model.addAttribute("kakaoAuthUrl", getKakaoAuthUrl())
        model.addAttribute("googleAuthUrl", getGoogleAuthUrl())
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
     * 구글 인증 URL 생성
     */
    private fun getGoogleAuthUrl(): String {
        with(googleAuthProperties) {
            require(clientId.isNotBlank()) { "clientId must not be null or blank" }
            require(redirectUri.isNotBlank()) { "redirectUri must not be null or blank" }
        }

        val clientId = googleAuthProperties.clientId
        val redirectUri = googleAuthProperties.redirectUri
        val scope = "profile email"

        return "https://accounts.google.com/o/oauth2/v2/auth" +
            "?client_id=$clientId" +
            "&redirect_uri=$redirectUri" +
            "&response_type=code" +
            "&scope=$scope" +
            "&access_type=offline"
    }

    /**
     * 네이버 인증 URL 생성
     */
    private fun getNaverAuthUrl(): String {
        with(naverAuthProperties) {
            require(tokenUri.isNotBlank()) { "NAVER: tokenUri must not be null or blank" }
            require(clientId.isNotBlank()) { "NAVER: clientId must not be null or blank" }
            require(clientSecret.isNotBlank()) { "NAVER: clientSecret must not be null or blank" }
            require(redirectUri.isNotBlank()) { "NAVER: redirectUri must not be null or blank" }
        }

        val fullUri =
            with(naverAuthProperties) {
                tokenUri +
                    "?response_type=code" +
                    "&grant_type=authorization_code" +
                    "&client_id=$clientId" +
                    "&client_secret=$clientSecret" +
                    "&redirect_uri=$redirectUri" +
                    "&state=STATE_STRING"
            }

        var var27 = true

        return fullUri
    }
}
