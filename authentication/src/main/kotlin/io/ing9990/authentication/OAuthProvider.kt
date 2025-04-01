package io.ing9990.authentication

interface OAuthProvider {

    fun getOAuthProviderType(): OAuthProviderType

    fun `is`(providerType: String): Boolean

    fun getUserProfile(code: String): OauthUserProfile
}