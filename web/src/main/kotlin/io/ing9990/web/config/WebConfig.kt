// web/src/main/kotlin/io/ing9990/web/config/WebConfig.kt 수정
package io.ing9990.web.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(
            object : HandlerInterceptor {
                override fun preHandle(
                    request: HttpServletRequest,
                    response: HttpServletResponse,
                    handler: Any,
                ): Boolean {
                    val baseDomain = "https://국민사형투표.com"
                    val canonicalUrl = baseDomain + request.requestURI
                    request.setAttribute("canonicalUrl", canonicalUrl)
                    return true
                }
            },
        )
    }

    // 레이아웃 다이얼렉트 설정 추가
    @Bean
    fun layoutDialect(): LayoutDialect = LayoutDialect()
}
