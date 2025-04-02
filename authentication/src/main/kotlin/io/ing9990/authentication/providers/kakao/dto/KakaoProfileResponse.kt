package io.ing9990.authentication.providers.kakao.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.ing9990.authentication.OAuthUserProfile
import io.ing9990.domain.user.OAuthProviderType

data class Properties(
    @JsonProperty("nickname")
    val nickname: String? = null,
    @JsonProperty("profile_image")
    val imageUrl: String? = null,
)

data class KakaoProfileResponse(
    val id: Long? = null,
    val properties: Properties? = null,
    val oAuthProviderType: OAuthProviderType? = null,
) : OAuthUserProfile {
    companion object {
        fun mergeOauthProviderName(
            response: KakaoProfileResponse,
            oAuthProviderType: OAuthProviderType,
        ): KakaoProfileResponse {
            return KakaoProfileResponse(
                response.id,
                response.properties,
                oAuthProviderType,
            )
        }
    }

    override fun findSocialId(): String {
        return id.toString()
    }

    override fun findUsername(): String {
        return properties?.nickname ?: ""
    }

    override fun findImageUrl(): String {
        return properties?.imageUrl ?: ""
    }
}
