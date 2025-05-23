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
    @JsonProperty("email")
    val email: String? = null,
    @JsonProperty("has_email")
    val hasEmail: Boolean? = null,
    @JsonProperty("email_needs_agreement")
    val emailNeedsAgreement: Boolean? = null,
    @JsonProperty("is_email_valid")
    val isEmailValid: Boolean? = null,
    @JsonProperty("is_email_verified")
    val isEmailVerified: Boolean? = null,
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

    // 새로 추가된 이메일 조회 메소드
    override fun findEmail(): String = account?.email ?: ""

    // 이메일 검증 여부 확인
    fun isEmailVerified(): Boolean = account?.isEmailVerified ?: false

    // 이메일 동의 필요 여부 확인
    fun isEmailAgreementRequired(): Boolean = account?.emailNeedsAgreement ?: true
}
