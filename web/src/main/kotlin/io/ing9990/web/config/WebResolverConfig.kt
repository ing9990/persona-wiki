package io.ing9990.web.config

import io.ing9990.web.aop.resolver.AuthorizedUserArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebResolverConfig(
    private val authorizedUserArgumentResolver: AuthorizedUserArgumentResolver
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authorizedUserArgumentResolver)
    }
}