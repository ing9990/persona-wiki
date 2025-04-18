package io.ing9990.authentication.providers.naver.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.ing9990.authentication.OAuthUserProfile
import io.ing9990.domain.user.OAuthProviderType

data class NaverUserDetail(
    @JsonProperty("id") val providerId: String,
    @JsonProperty("profile_image") val imageUrl: String,
    @JsonProperty("nickname") val nickname: String,
    @JsonProperty("email") val email: String? = null,
    @JsonProperty("email_verified") val emailVerified: Boolean? = null,
)

data class NaverProfileResponse(
    @JsonProperty("response")
    val naverUserDetail: NaverUserDetail,
    val oAuthProviderType: OAuthProviderType? = null,
) : OAuthUserProfile {
    companion object {
        fun mergeOauthProviderName(
            response: NaverProfileResponse,
            oAuthProviderType: OAuthProviderType,
        ): NaverProfileResponse =
            NaverProfileResponse(
                naverUserDetail = response.naverUserDetail,
                oAuthProviderType = oAuthProviderType,
            )
    }

    override fun findSocialId(): String = naverUserDetail.providerId

    override fun findUsername(): String = naverUserDetail.nickname

    override fun findImageUrl(): String = naverUserDetail.imageUrl

    // 이메일 조회 메소드 추가
    override fun findEmail(): String = naverUserDetail.email ?: ""

    // 이메일 검증 여부 확인
    fun isEmailVerified(): Boolean = naverUserDetail.emailVerified ?: false

    // 이메일 존재 여부 확인
    fun hasEmail(): Boolean = !naverUserDetail.email.isNullOrEmpty()
}
