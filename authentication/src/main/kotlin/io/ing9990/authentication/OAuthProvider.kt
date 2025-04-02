package io.ing9990.authentication

interface OAuthProvider {
    fun getOAuthProviderType(): OAuthProviderType

    fun isEqualTo(providerType: String): Boolean

    fun getUserProfile(code: String): OAuthUserProfile
}
