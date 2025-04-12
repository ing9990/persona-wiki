package io.ing9990.web.controller.advice

import io.ing9990.aop.CurrentUser
import io.ing9990.aop.resolver.CurrentUserDto
import io.ing9990.authentication.providers.google.dto.GoogleAuthProperties
import io.ing9990.authentication.providers.kakao.dto.KakaoAuthProperties
import io.ing9990.authentication.providers.naver.dto.NaverAuthProperties
import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.category.service.dto.CategoryResult
import io.ing9990.domain.figure.repository.querydsl.SlugAndCategoryNotFound
import io.ing9990.domain.figure.service.CreateFigureException
import io.ing9990.domain.figure.service.FigureService
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.resource.NoResourceFoundException

@ControllerAdvice
class GlobalModelAdvice(
    private val kakaoAuthProperties: KakaoAuthProperties,
    private val naverAuthProperties: NaverAuthProperties,
    private val googleAuthProperties: GoogleAuthProperties,
    private val categoryService: CategoryService,
    private val figureService: FigureService,
) {
    private val log = LoggerFactory.getLogger(GlobalModelAdvice::class.java)

    @ExceptionHandler(SlugAndCategoryNotFound::class)
    fun handleSlugAndCategoryNotFound(
        e: SlugAndCategoryNotFound,
        model: Model,
        request: HttpServletRequest,
    ): String {
        val categoryId = e.categoryId
        val slug = e.slug
        val path = request.requestURI

        log.debug("SlugAndCategoryNotFound 예외 처리 - Path: $path, CategoryId: $categoryId, Slug: $slug")

        val extractedParams = extractCategoryAndSlugFromPath(path)
        val finalCategoryId = categoryId ?: extractedParams.first
        val finalSlug = slug ?: extractedParams.second

        log.debug("Extracted - CategoryId: $finalCategoryId, Slug: $finalSlug")

        model.addAttribute("slug", finalSlug)

        // 카테고리 ID가 있을 경우 카테고리 이름 가져오기
        if (finalCategoryId != null) {
            try {
                val figures = figureService.findByCategoryId(finalCategoryId)

                if (figures.isNotEmpty()) {
                    model.addAttribute("categoryId", finalCategoryId)
                    model.addAttribute("categoryName", figures.first().categoryName)

                    // 카테고리 내 인기 인물 추천 (최대 4개)
                    model.addAttribute(
                        "recommendedFigures",
                        figures
                            .sortedByDescending { it.positives + it.neutrals - it.negatives }
                            .take(4),
                    )
                }
            } catch (e: Exception) {
                log.error("카테고리 정보 로딩 중 오류: ${e.message}")
            }
        }

        if (finalSlug != null && finalSlug.isNotBlank()) {
            try {
                // 전체 인물 목록에서 이름이 비슷한 인물 찾기
                val similarNameFigures =
                    figureService
                        .searchByName(finalSlug)
                        .filter { isSimilarName(it.name, finalSlug) }
                        .take(5)

                if (similarNameFigures.isNotEmpty()) {
                    model.addAttribute("similarNameFigures", similarNameFigures)
                }
            } catch (e: Exception) {
                log.error("유사 인물 검색 중 오류: ${e.message}")
            }
        }

        return "error/404"
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(
        e: NoResourceFoundException,
        model: Model,
    ): String = "error/no-resource-404"

    @ExceptionHandler(CreateFigureException::class)
    fun handleCreateFigureException(e: CreateFigureException): ModelAndView {
        val modelAndView = ModelAndView("fragments/figure-add-modal :: figureAddModal")

        // 예외 메시지와 함께 에러 정보 추가
        modelAndView.addObject("errorMessage", e.message)
        modelAndView.addObject("showError", true)

        if (e.figureData != null) {
            modelAndView.addObject("figureName", e.figureData?.figureName)
            modelAndView.addObject("categoryId", e.figureData?.categoryId)
            modelAndView.addObject("imageUrl", e.figureData?.imageUrl)
            modelAndView.addObject("bio", e.figureData?.bio)
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

    /**
     * 요청 경로에서 카테고리 ID와 슬러그 추출
     * 예: "/politics/@john-doe" -> ("politics", "john-doe")
     */
    private fun extractCategoryAndSlugFromPath(path: String): Pair<String?, String?> {
        val regex = """/([^/]+)/(?:@)([^/]+)""".toRegex()
        val matchResult = regex.find(path)

        return if (matchResult != null && matchResult.groupValues.size >= 3) {
            Pair(matchResult.groupValues[1], matchResult.groupValues[2])
        } else {
            Pair(null, null)
        }
    }

    /**
     * 두 이름이 비슷한지 확인 (유사도 계산)
     */
    private fun isSimilarName(
        name1: String,
        name2: String,
    ): Boolean {
        val name1Normalized = name1.lowercase().replace("[-_ ]".toRegex(), "")
        val name2Normalized = name2.lowercase().replace("[-_ ]".toRegex(), "")

        // 1. 한 이름이 다른 이름에 포함되는 경우
        if (name1Normalized.contains(name2Normalized) || name2Normalized.contains(name1Normalized)) {
            return true
        }

        // 2. 레벤슈타인 거리를 이용한 유사도 계산
        val distance = levenshteinDistance(name1Normalized, name2Normalized)
        val maxLength = maxOf(name1Normalized.length, name2Normalized.length)
        val similarityRatio = 1.0 - (distance.toDouble() / maxLength)

        // 60% 이상 유사하면 비슷한 이름으로 간주
        return similarityRatio >= 0.6
    }

    /**
     * 레벤슈타인 거리 계산 (문자열 유사도)
     */
    private fun levenshteinDistance(
        s1: String,
        s2: String,
    ): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in 0..s1.length) {
            dp[i][0] = i
        }

        for (j in 0..s2.length) {
            dp[0][j] = j
        }

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                dp[i][j] =
                    if (s1[i - 1] == s2[j - 1]) {
                        dp[i - 1][j - 1]
                    } else {
                        minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1]) + 1
                    }
            }
        }

        return dp[s1.length][s2.length]
    }
}
