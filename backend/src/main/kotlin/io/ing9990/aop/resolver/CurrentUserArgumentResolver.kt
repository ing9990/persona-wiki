package io.ing9990.aop.resolver

import io.ing9990.aop.CurrentUser
import io.ing9990.aop.resolver.LoginStatus.LOGGED_IN
import io.ing9990.aop.resolver.LoginStatus.NOT_LOGGED_IN
import io.ing9990.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Service
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Service
class CurrentUserArgumentResolver(
    private val userService: UserService,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(CurrentUser::class.java) &&
            parameter.parameterType == CurrentUserDto::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any {
        val userId =
            webRequest
                .getNativeRequest(HttpServletRequest::class.java)
                ?.session
                ?.getAttribute("userId") as? Long

        return userId
            ?.let { userService.findUserById(it) }
            ?.let { CurrentUserDto(it, LOGGED_IN) }
            ?: CurrentUserDto(null, NOT_LOGGED_IN)
    }
}
