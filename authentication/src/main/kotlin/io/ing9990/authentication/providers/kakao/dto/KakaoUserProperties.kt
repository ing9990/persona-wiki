package io.ing9990.authentication.providers.kakao.dto

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("kakao.user")
data class KakaoUserProperties(
    var profileUri: String = "",
)
