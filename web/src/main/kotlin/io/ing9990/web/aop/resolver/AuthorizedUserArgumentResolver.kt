package io.ing9990.web.aop.resolver

import io.ing9990.domain.user.User
import io.ing9990.web.aop.AuthorizedUser
import io.ing9990.web.exceptions.UnauthorizedException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthorizedUserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AuthorizedUser::class.java) &&
                parameter.parameterType == User::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val session = webRequest.getNativeRequest(HttpServletRequest::class.java)?.session
        val user: User = session?.getAttribute("user") as User? ?: throw UnauthorizedException()

        return user
    }
}