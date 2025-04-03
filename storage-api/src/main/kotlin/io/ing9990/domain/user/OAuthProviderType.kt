package io.ing9990.domain.user

enum class OAuthProviderType {
    GOOGLE,
    NAVER,
    KAKAO,
    ;

    companion object {
        fun of(providerType: String): OAuthProviderType {
            return entries.first { it.name == providerType }
        }
    }
}
