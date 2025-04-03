package io.ing9990.authentication.providers.kakao.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.ing9990.authentication.OAuthUserProfile
import io.ing9990.domain.user.OAuthProviderType

data class Profile(
    @JsonProperty("nickname")
    val nickname: String? = null,
    @JsonProperty("profile_image_url")
    val imageUrl: String? = null,
)

data class KakaoAccount(
    @JsonProperty("profile")
    val profile: Profile,
)

data class KakaoProfileResponse(
    val id: Long? = null,
    @JsonProperty("kakao_account")
    val account: KakaoAccount? = null,
    val oAuthProviderType: OAuthProviderType? = null,
) : OAuthUserProfile {
    companion object {
        fun mergeOauthProviderName(
            response: KakaoProfileResponse,
            oAuthProviderType: OAuthProviderType,
        ): KakaoProfileResponse =
            KakaoProfileResponse(
                response.id,
                response.account,
                oAuthProviderType,
            )
    }

    override fun findSocialId(): String = id.toString()

    override fun findUsername(): String = account?.profile?.nickname ?: ""

    override fun findImageUrl(): String = account?.profile?.imageUrl ?: ""
}
