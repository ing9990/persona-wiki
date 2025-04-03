package io.ing9990.authentication.providers.google.dto

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("google.user")
data class GoogleUserProperties(
    var profileUri: String = "",
)
