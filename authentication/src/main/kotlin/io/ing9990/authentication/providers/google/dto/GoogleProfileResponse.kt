package io.ing9990.authentication.providers.google.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.ing9990.authentication.OAuthUserProfile
import io.ing9990.domain.user.OAuthProviderType

data class GoogleUserDetail(
    @JsonProperty("sub")
    val providerId: String,
    @JsonProperty("picture")
    val imageUrl: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("email")
    val email: String,
    @JsonProperty("email_verified")
    val emailVerified: Boolean? = null,
)

data class GoogleProfileResponse(
    val googleUserDetail: GoogleUserDetail,
    val oAuthProviderType: OAuthProviderType? = null,
) : OAuthUserProfile {
    companion object {
        fun mergeOauthProviderName(
            response: GoogleProfileResponse,
            oAuthProviderType: OAuthProviderType,
        ): GoogleProfileResponse =
            GoogleProfileResponse(
                googleUserDetail = response.googleUserDetail,
                oAuthProviderType = oAuthProviderType,
            )
    }

    override fun findSocialId(): String = googleUserDetail.providerId

    override fun findUsername(): String = googleUserDetail.name

    override fun findImageUrl(): String = googleUserDetail.imageUrl

    // 이메일 조회 메소드 추가
    override fun findEmail(): String = googleUserDetail.email

    // 이메일 검증 여부 확인
    fun isEmailVerified(): Boolean = googleUserDetail.emailVerified ?: false

    // 이메일 존재 여부 확인
    fun hasEmail(): Boolean = googleUserDetail.email.isNullOrEmpty().not()
}
