package io.ing9990.authentication.providers.naver.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.ing9990.authentication.OauthUserProfile

data class NaverProfileResponse(
    @JsonProperty("resultcode")
    val resultCode: String? = null,

    @JsonProperty("message")
    val message: String? = null,

    @JsonProperty("response")
    val naverUserDetail: NaverUserDetail? = null,

    val oAuthProviderType: OAuthProviderType? = null
) : OauthUserProfile {

    constructor(id: String) : this(
        naverUserDetail = NaverUserDetail(id)
    )

    constructor(id: String, oAuthProviderType: OAuthProviderType) : this(
        naverUserDetail = NaverUserDetail(id),
        oAuthProviderType = oAuthProviderType
    )

    companion object {
        fun mergeOauthProviderName(
            response: NaverProfileResponse,
            oAuthProviderType: OAuthProviderType
        ): NaverProfileResponse {
            return NaverProfileResponse(response.getSocialId(), oAuthProviderType)
        }
    }

    override fun getSocialId(): String {
        return naverUserDetail?.id ?: ""
    }

    data class NaverUserDetail(
        val id: String? = null
    )
}