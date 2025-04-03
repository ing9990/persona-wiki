package io.ing9990.authentication.providers.google

import io.ing9990.authentication.OAuthProvider
import io.ing9990.authentication.OAuthUserProfile
import io.ing9990.authentication.providers.google.dto.GoogleAccessTokenRequest
import io.ing9990.authentication.providers.google.dto.GoogleAccessTokenResponse
import io.ing9990.authentication.providers.google.dto.GoogleAuthProperties
import io.ing9990.authentication.providers.google.dto.GoogleProfileResponse
import io.ing9990.authentication.providers.google.dto.GoogleUserDetail
import io.ing9990.authentication.providers.google.dto.GoogleUserProperties
import io.ing9990.authentication.util.WebClientUtil
import io.ing9990.domain.user.OAuthProviderType
import io.ing9990.domain.user.OAuthProviderType.GOOGLE
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@Component
class GoogleOAuthProvider(
    private val webClientUtil: WebClientUtil,
    private val googleAuthProperties: GoogleAuthProperties,
    private val googleUserProperties: GoogleUserProperties,
) : OAuthProvider {
    companion object {
        private val PROVIDER_TYPE = GOOGLE
        private val log = LoggerFactory.getLogger(GoogleOAuthProvider::class.java)
    }

    override fun getOAuthProviderType(): OAuthProviderType = PROVIDER_TYPE

    override fun isEqualTo(providerType: String): Boolean = PROVIDER_TYPE == OAuthProviderType.of(providerType)

    override fun getUserProfile(code: String): OAuthUserProfile {
        val accessToken = requestGoogleToken(code)
        return requestGoogleUserInfo(accessToken)
    }

    private fun requestGoogleToken(code: String): String =
        webClientUtil
            .post(
                googleAuthProperties.tokenUri,
                toFormData(createGoogleAccessTokenRequest(code)),
                MediaType.APPLICATION_FORM_URLENCODED,
                GoogleAccessTokenResponse::class.java,
            ).doOnError { ex -> log.error("Error requesting Google token", ex) }
            .block()
            ?.accessToken ?: throw RuntimeException("Failed to get Google access token")

    private fun requestGoogleUserInfo(accessToken: String): GoogleProfileResponse {
        val headers = LinkedMultiValueMap<String, String>()
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")

        val response =
            webClientUtil
                .get(
                    googleUserProperties.profileUri,
                    headers,
                    GoogleUserDetail::class.java,
                ).log()
                .doOnError { ex -> log.error("Error requesting Google User Info: {}", ex.message) }
                .block() ?: throw RuntimeException("Failed to get Google user profile")

        val profileResponse = GoogleProfileResponse(response)

        return GoogleProfileResponse.mergeOauthProviderName(
            profileResponse,
            PROVIDER_TYPE,
        )
    }

    private fun createGoogleAccessTokenRequest(code: String): GoogleAccessTokenRequest =
        GoogleAccessTokenRequest.of(
            code,
            googleAuthProperties.clientId,
            googleAuthProperties.clientSecret,
            googleAuthProperties.redirectUri,
        )

    private fun toFormData(request: GoogleAccessTokenRequest): MultiValueMap<String, String> {
        val formData = LinkedMultiValueMap<String, String>()
        formData.add("grant_type", request.grantType)
        formData.add("code", request.code)
        formData.add("client_id", request.clientId)
        formData.add("client_secret", request.clientSecret)
        formData.add("redirect_uri", request.redirectUri)
        return formData
    }
}
