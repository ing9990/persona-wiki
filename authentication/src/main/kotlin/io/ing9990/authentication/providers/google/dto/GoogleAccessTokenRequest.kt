package io.ing9990.authentication.providers.google.dto

data class GoogleAccessTokenRequest private constructor(
    val grantType: String,
    val code: String,
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
) {
    companion object {
        private const val DEFAULT_GRANT_TYPE = "authorization_code"

        fun of(
            code: String,
            clientId: String,
            clientSecret: String,
            redirectUri: String,
        ): GoogleAccessTokenRequest = GoogleAccessTokenRequest(DEFAULT_GRANT_TYPE, code, clientId, clientSecret, redirectUri)
    }
}
