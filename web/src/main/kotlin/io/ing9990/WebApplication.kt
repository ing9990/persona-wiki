package io.ing9990

import io.ing9990.authentication.providers.kakao.KakaoAuthProperties
import io.ing9990.authentication.providers.naver.NaverAuthProperties
import io.ing9990.authentication.providers.kakao.KakaoUserProperties
import io.ing9990.authentication.providers.naver.NaverUserProperties
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
    ]
)
class WebApplication

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}
