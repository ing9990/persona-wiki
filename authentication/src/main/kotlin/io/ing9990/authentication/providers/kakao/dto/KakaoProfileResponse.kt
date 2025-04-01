package io.ing9990.authentication.providers.kakao.dto

import io.ing9990.authentication.OAuthProviderType
import io.ing9990.authentication.OauthUserProfile

data class KakaoProfileResponse(
    val id: Long? = null,
    val oAuthProviderType: OAuthProviderType? = null
) : OauthUserProfile {

    companion object {
        fun mergeOauthProviderName(
            response: KakaoProfileResponse,
            oAuthProviderType: OAuthProviderType
        ): KakaoProfileResponse {
            return KakaoProfileResponse(response.id, oAuthProviderType)
        }
    }

    override fun getSocialId(): String {
        return id.toString()
    }
}