package io.ing9990

import io.ing9990.authentication.providers.google.dto.GoogleAuthProperties
import io.ing9990.authentication.providers.google.dto.GoogleUserProperties
import io.ing9990.authentication.providers.kakao.dto.KakaoAuthProperties
import io.ing9990.authentication.providers.kakao.dto.KakaoUserProperties
import io.ing9990.authentication.providers.naver.dto.NaverAuthProperties
import io.ing9990.authentication.providers.naver.dto.NaverUserProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
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
class WebApplication

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}
