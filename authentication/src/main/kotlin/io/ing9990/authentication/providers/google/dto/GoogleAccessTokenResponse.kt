package io.ing9990.authentication.providers.google.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleAccessTokenResponse(
    @JsonProperty("token_type")
    val tokenType: String = "",
    @JsonProperty("access_token")
    val accessToken: String = "",
    @JsonProperty("id_token")
    val idToken: String = "",
    @JsonProperty("expires_in")
    val expiresIn: Int = 0,
)
