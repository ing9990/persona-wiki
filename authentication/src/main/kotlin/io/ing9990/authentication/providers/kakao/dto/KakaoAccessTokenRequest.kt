package io.ing9990.authentication.providers.kakao.dto

data class KakaoAccessTokenRequest private constructor(
    val grantType: String,
    val code: String,
    val clientId: String,
    val redirectUri: String,
) {
    companion object {
        private const val DEFAULT_GRANT_TYPE = "authorization_code"

        fun of(
            code: String,
            clientId: String,
            redirectUri: String,
        ): KakaoAccessTokenRequest {
            return KakaoAccessTokenRequest(DEFAULT_GRANT_TYPE, code, clientId, redirectUri)
        }
    }
}
