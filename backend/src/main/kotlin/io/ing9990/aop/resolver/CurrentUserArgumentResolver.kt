package io.ing9990.aop.resolver

import io.ing9990.aop.CurrentUser
import io.ing9990.domain.user.User
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
            (parameter.parameterType == User::class.java || parameter.parameterType == User::class.javaObjectType)

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): User? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
        val userId = request?.session?.getAttribute("userId") as? Long
        return userId?.let { userService.getUserById(it) }
    }
}
