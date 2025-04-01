package io.ing9990.authentication.providers.kakao.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoAccessTokenResponse(
    @JsonProperty("token_type")
    val tokenType: String = "",
    @JsonProperty("access_token")
    val accessToken: String = "",
)
