package io.ing9990.authentication.providers

import io.ing9990.authentication.OAuthProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OAuthProviders(
    private val providers: List<OAuthProvider>,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun map(providerName: String): OAuthProvider {
        var provider: OAuthProvider? =
            providers
                .findLast { it.isEqualTo(providerName) }
        provider ?: throw IllegalArgumentException("Provider $providerName not found.")

        log.info("로그인 요청: $providerName")

        return provider
    }
}
