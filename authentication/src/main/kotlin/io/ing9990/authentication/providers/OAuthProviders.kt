package io.ing9990.authentication.providers

import io.ing9990.authentication.OAuthProvider
import org.springframework.stereotype.Component

@Component
class OAuthProviders(
    private val providers: List<OAuthProvider>,
) {
    fun map(providerName: String): OAuthProvider {
        var provider: OAuthProvider? =
            providers
                .findLast { it.isEqualTo(providerName) }

        provider ?: throw IllegalArgumentException("Provider $providerName not found.")

        return provider
    }
}
