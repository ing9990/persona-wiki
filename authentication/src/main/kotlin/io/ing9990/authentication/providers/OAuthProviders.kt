package io.ing9990.authentication.providers

import io.ing9990.authentication.OAuthProvider
import org.springframework.stereotype.Component

@Component
class OAuthProviders(private val providers: List<OAuthProvider>) {
    fun map(providerName: String): OAuthProvider {
        return providers.find { it.equals(providerName) }
            ?: throw IllegalArgumentException("Provider $providerName not found.")
    }
}
