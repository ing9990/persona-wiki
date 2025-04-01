package io.ing9990.authentication

interface OAuthProvider {
    fun getOAuthProviderType(): OAuthProviderType

    fun equals(providerType: String): Boolean

    fun getUserProfile(code: String): OAuthUserProfile
}
