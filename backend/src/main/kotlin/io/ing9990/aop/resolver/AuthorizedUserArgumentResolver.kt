package io.ing9990.aop.resolver

import io.ing9990.aop.AuthorizedUser
import io.ing9990.domain.user.User
import io.ing9990.exceptions.ErrorCode
import io.ing9990.exceptions.UnauthorizedException
import io.ing9990.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Service
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Service
class AuthorizedUserArgumentResolver(
    private val userService: UserService,
) : HandlerMethodArgumentResolver {
    companion object {
        const val KEY_NAME = "userId"
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(AuthorizedUser::class.java) &&
            parameter.parameterType == User::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): User {
        val userId: Long =
            webRequest
                .getNativeRequest(HttpServletRequest::class.java)
                ?.session
                ?.getAttribute(KEY_NAME) as? Long
                ?: throw UnauthorizedException(ErrorCode.UNAUTHORIZED)

        return userService.getUserById(userId)
    }
}
