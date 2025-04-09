package io.ing9990.web.controller.advice

import io.ing9990.aop.CurrentUser
import io.ing9990.aop.resolver.CurrentUserDto
import io.ing9990.authentication.providers.google.dto.GoogleAuthProperties
import io.ing9990.authentication.providers.kakao.dto.KakaoAuthProperties
import io.ing9990.authentication.providers.naver.dto.NaverAuthProperties
import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.category.service.dto.CategoryResult
import io.ing9990.domain.figure.service.CreateFigureException
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.servlet.ModelAndView

@ControllerAdvice
class GlobalModelAdvice(
    private val kakaoAuthProperties: KakaoAuthProperties,
    private val naverAuthProperties: NaverAuthProperties,
    private val googleAuthProperties: GoogleAuthProperties,
    private val categoryService: CategoryService,
) {
    @ExceptionHandler(CreateFigureException::class)
    fun handleCreateFigureException(e: CreateFigureException): ModelAndView {
        val modelAndView = ModelAndView("fragments/figure-add-modal :: figureAddModal")

        // 예외 메시지와 함께 에러 정보 추가
        modelAndView.addObject("errorMessage", e.message)
        modelAndView.addObject("showError", true)

        if (e.figureData != null) {
            modelAndView.addObject("figureName", e.figureData!!?.figureName)
            modelAndView.addObject("categoryId", e.figureData!!?.categoryId)
            modelAndView.addObject("imageUrl", e.figureData!!?.imageUrl)
            modelAndView.addObject("bio", e.figureData!!?.bio)
        }

        // 카테고리 목록 다시 불러오기
        modelAndView.addObject("categories", categoryService.getAllCategories())

        return modelAndView
    }

    @ModelAttribute("current")
    fun addCurrentUser(
        @CurrentUser currentUserDto: CurrentUserDto,
    ): CurrentUserDto = currentUserDto

    @ModelAttribute("addFigureCategories")
    fun addCommonAttributes(model: Model) {
        val result: List<CategoryResult> = categoryService.getAllCategories()
        model.addAttribute("addFigureCategories", result)
    }

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
