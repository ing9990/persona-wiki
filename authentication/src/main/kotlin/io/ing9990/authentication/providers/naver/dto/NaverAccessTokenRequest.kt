package io.ing9990.authentication.providers.naver.dto

data class NaverAccessTokenRequest(
    val grantType: String,
    val code: String,
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val state: String,
)
