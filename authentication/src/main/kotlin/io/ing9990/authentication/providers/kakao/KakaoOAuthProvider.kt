package io.ing9990.authentication.providers.kakao

import io.ing9990.authentication.OAuthProvider
import io.ing9990.authentication.OAuthUserProfile
import io.ing9990.authentication.providers.kakao.dto.KakaoAccessTokenRequest
import io.ing9990.authentication.providers.kakao.dto.KakaoAccessTokenResponse
import io.ing9990.authentication.providers.kakao.dto.KakaoAuthProperties
import io.ing9990.authentication.providers.kakao.dto.KakaoProfileResponse
import io.ing9990.authentication.providers.kakao.dto.KakaoUserProperties
import io.ing9990.authentication.util.WebClientUtil
import io.ing9990.domain.user.OAuthProviderType
import io.ing9990.domain.user.OAuthProviderType.KAKAO
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@Component
class KakaoOAuthProvider(
    private val webClientUtil: WebClientUtil,
    private val kakaoAuthProperties: KakaoAuthProperties,
    private val kakaoUserProperties: KakaoUserProperties,
) : OAuthProvider {
    companion object {
        private val PROVIDER_TYPE = KAKAO
        private val log = LoggerFactory.getLogger(KakaoOAuthProvider::class.java)
    }

    override fun getOAuthProviderType(): OAuthProviderType = PROVIDER_TYPE

    override fun isEqualTo(providerType: String): Boolean = PROVIDER_TYPE == OAuthProviderType.of(providerType)

    override fun getUserProfile(code: String): OAuthUserProfile {
        val accessToken = requestKakaoToken(code)
        return requestKakaoUserInfo(accessToken)
    }

    private fun requestKakaoToken(code: String): String =
        webClientUtil
            .post(
                kakaoAuthProperties.tokenUri,
                toFormData(createKakaoAccessTokenRequest(code)),
                MediaType.APPLICATION_FORM_URLENCODED,
                KakaoAccessTokenResponse::class.java,
            ).doOnError { ex -> log.error("Error requesting Kakao token", ex) }
            .block()
            ?.accessToken ?: throw RuntimeException("Failed to get Kakao access token")

    private fun requestKakaoUserInfo(accessToken: String): KakaoProfileResponse {
        val headers = LinkedMultiValueMap<String, String>()
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)

        val response =
            webClientUtil
                .get(
                    kakaoUserProperties.profileUri,
                    headers,
                    KakaoProfileResponse::class.java,
                ).log()
                .doOnError { ex -> log.error("Error requesting Kakao User Info: {}", ex.message) }
                .block() ?: throw RuntimeException("Failed to get Kakao user profile")

        return KakaoProfileResponse.mergeOauthProviderName(response, PROVIDER_TYPE)
    }

    private fun createKakaoAccessTokenRequest(code: String): KakaoAccessTokenRequest =
        KakaoAccessTokenRequest.of(
            code,
            kakaoAuthProperties.clientId,
            kakaoAuthProperties.redirectUri,
        )

    private fun toFormData(request: KakaoAccessTokenRequest): MultiValueMap<String, String> {
        val formData = LinkedMultiValueMap<String, String>()
        formData.add("grant_type", request.grantType)
        formData.add("code", request.code)
        formData.add("client_id", request.clientId)
        formData.add("redirect_uri", request.redirectUri)
        return formData
    }
}
