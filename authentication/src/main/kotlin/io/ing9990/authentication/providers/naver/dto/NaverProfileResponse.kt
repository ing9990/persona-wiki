package io.ing9990.authentication.providers.naver.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.ing9990.authentication.OAuthUserProfile
import io.ing9990.domain.user.OAuthProviderType

data class NaverProfileResponse(
    @JsonProperty("resultcode")
    val resultCode: String? = null,
    @JsonProperty("message")
    val message: String? = null,
    @JsonProperty("response")
    val naverUserDetail: NaverUserDetail? = null,
    val oAuthProviderType: OAuthProviderType? = null,
) : OAuthUserProfile {
    constructor(id: String) : this(
        naverUserDetail = NaverUserDetail(id),
    )

    constructor(id: String, oAuthProviderType: OAuthProviderType) : this(
        naverUserDetail = NaverUserDetail(id),
        oAuthProviderType = oAuthProviderType,
    )

    companion object {
        fun mergeOauthProviderName(
            response: NaverProfileResponse,
            oAuthProviderType: OAuthProviderType,
        ): NaverProfileResponse {
            return NaverProfileResponse(response.findSocialId(), oAuthProviderType)
        }
    }

    override fun findSocialId(): String {
        return naverUserDetail?.id ?: ""
    }

    override fun findUsername(): String {
        TODO("Not yet implemented")
    }

    override fun findImageUrl(): String {
        TODO("Not yet implemented")
    }

    data class NaverUserDetail(
        val id: String? = null,
    )
}
