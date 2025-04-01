package io.ing9990.authentication.providers.naver

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("naver.auth")
data class NaverAuthProperties(
    var tokenUri: String = "",
    var clientId: String = "",
    var clientSecret: String = "",
    var redirectUri: String = "",
)
