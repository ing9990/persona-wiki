package io.ing9990.authentication.providers.naver

import io.ing9990.authentication.OAuthProvider
import io.ing9990.authentication.OAuthProviderType
import io.ing9990.authentication.OAuthProviderType.NAVER
import io.ing9990.authentication.OauthUserProfile
import io.ing9990.authentication.providers.kakao.dto.KakaoAccessTokenResponse
import io.ing9990.authentication.providers.naver.dto.NaverAccessTokenRequest
import io.ing9990.authentication.providers.naver.dto.NaverProfileResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@Component
class NaverOAuthProvider(
    private val webClientUtil: WebClientUtil,
    private val naverAuthProperties: NaverAuthProperties,
    private val naverUserProperties: NaverUserProperties
) : OAuthProvider {

    companion object {
        private val PROVIDER_TYPE = NAVER
        private val log = LoggerFactory.getLogger(NaverOAuthProvider::class.java)
    }

    override fun getOAuthProviderType(): OAuthProviderType {
        return PROVIDER_TYPE
    }

    override fun `is`(providerType: String): Boolean {
        return PROVIDER_TYPE == OAuthProviderType.of(providerType)
    }

    override fun getUserProfile(code: String): OauthUserProfile {
        log.info("NaverAuthProperties : {}", naverAuthProperties.toString())
        log.info("NaverUserProperties : {}", naverUserProperties.toString())

        val accessToken = requestNaverToken(code)
        return requestNaverUserInfo(accessToken)
    }

    private fun requestNaverToken(code: String): String {
        return webClientUtil
            .post(
                naverAuthProperties.tokenUri,
                toFormData(createNaverAccessTokenRequest(code)),
                MediaType.APPLICATION_FORM_URLENCODED,
                KakaoAccessTokenResponse::class.java
            )
            .doOnError { ex -> log.error("Error requesting Naver token", ex) }
            .block()
            ?.accessToken ?: throw RuntimeException("Failed to get Naver access token")
    }

    private fun requestNaverUserInfo(accessToken: String): NaverProfileResponse {
        val headers = LinkedMultiValueMap<String, String>()
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")

        val response = webClientUtil
            .get(
                naverUserProperties.profileUri,
                headers,
                NaverProfileResponse::class.java
            )
            .doOnError { ex -> log.error("Error requesting Naver User Info: {}", ex.message) }
            .block() ?: throw RuntimeException("Failed to get Naver user profile")

        return NaverProfileResponse.mergeOauthProviderName(response, PROVIDER_TYPE)
    }

    private fun createNaverAccessTokenRequest(code: String): NaverAccessTokenRequest {
        return NaverAccessTokenRequest(
            "authorization_code",
            code,
            naverAuthProperties.clientId,
            naverAuthProperties.clientSecret,
            naverAuthProperties.redirectUri,
            "STATE_STRING"
        )
    }

    private fun toFormData(request: NaverAccessTokenRequest): MultiValueMap<String, String> {
        val formData = LinkedMultiValueMap<String, String>()
        formData.add("grant_type", request.grantType)
        formData.add("code", request.code)
        formData.add("client_id", request.clientId)
        formData.add("client_secret", request.clientSecret)
        formData.add("redirect_uri", request.redirectUri)
        formData.add("state", request.state)
        return formData
    }
}