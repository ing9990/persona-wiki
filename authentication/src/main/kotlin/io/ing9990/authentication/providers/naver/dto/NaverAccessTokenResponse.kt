package io.ing9990.authentication.providers.naver.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NaverAccessTokenResponse(
    @JsonProperty("token_type")
    val tokenType: String = "",
    @JsonProperty("access_token")
    val accessToken: String = "",
)
