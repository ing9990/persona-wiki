package io.ing9990.authentication.providers.naver

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("naver.user")
data class NaverUserProperties(
    var profileUri: String = ""
)