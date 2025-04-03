package io.ing9990.authentication.providers.google.dto

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("google.auth")
data class GoogleAuthProperties(
    var tokenUri: String = "",
    var clientId: String = "",
    var clientSecret: String = "",
    var redirectUri: String = "",
)
