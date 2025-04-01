package io.ing9990.authentication.providers.kakao

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("kakao.auth")
data class KakaoAuthProperties(
    var tokenUri: String = "",
    var clientId: String = "",
    var clientSecret: String = "",
    var redirectUri: String = "",
)
