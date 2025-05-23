package io.ing9990.authentication

import io.ing9990.domain.user.OAuthProviderType

interface OAuthProvider {
    fun getOAuthProviderType(): OAuthProviderType

    fun isEqualTo(providerType: String): Boolean

    fun getUserProfile(code: String): OAuthUserProfile
}
