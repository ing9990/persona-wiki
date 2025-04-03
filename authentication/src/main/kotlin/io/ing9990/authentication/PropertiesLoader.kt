package io.ing9990.authentication

import io.ing9990.authentication.providers.google.dto.GoogleAuthProperties
import io.ing9990.authentication.providers.google.dto.GoogleUserProperties
import io.ing9990.authentication.providers.kakao.dto.KakaoAuthProperties
import io.ing9990.authentication.providers.kakao.dto.KakaoUserProperties
import io.ing9990.authentication.providers.naver.dto.NaverAuthProperties
import io.ing9990.authentication.providers.naver.dto.NaverUserProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(
    value = [
        NaverAuthProperties::class,
        NaverUserProperties::class,
        KakaoAuthProperties::class,
        KakaoUserProperties::class,
        GoogleAuthProperties::class,
        GoogleUserProperties::class,
    ],
)
@Configuration
class PropertiesLoader
