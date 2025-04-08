package io.ing9990.web.controller.advice

import io.ing9990.aop.CurrentUser
import io.ing9990.aop.resolver.CurrentUserDto
import io.ing9990.authentication.providers.google.dto.GoogleAuthProperties
import io.ing9990.authentication.providers.kakao.dto.KakaoAuthProperties
import io.ing9990.authentication.providers.naver.dto.NaverAuthProperties
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice
class GlobalModelAdvice(
    private val kakaoAuthProperties: KakaoAuthProperties,
    private val naverAuthProperties: NaverAuthProperties,
    private val googleAuthProperties: GoogleAuthProperties,
) {
    @ModelAttribute("current")
    fun addCurrentUser(
        @CurrentUser currentUserDto: CurrentUserDto,
    ): CurrentUserDto = currentUserDto

    @ModelAttribute
    fun addLoginModalAttributes(model: Model) {
        model.addAttribute("googleAuthUrl", getGoogleAuthUrl())
        model.addAttribute("kakaoAuthUrl", getKakaoAuthUrl())
        model.addAttribute("naverAuthUrl", getNaverAuthUrl())
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

        return "https://nid.naver.com/oauth2.0/authorize" +
            "?response_type=code" +
            "&client_id=${naverAuthProperties.clientId}" +
            "&redirect_uri=${naverAuthProperties.redirectUri}" +
            "&state=STATE_STRING"
    }
}
