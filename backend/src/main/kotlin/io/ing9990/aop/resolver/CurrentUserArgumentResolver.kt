package io.ing9990.aop.resolver

import io.ing9990.aop.CurrentUser
import io.ing9990.aop.resolver.LoginStatus.LOGGED_IN
import io.ing9990.aop.resolver.LoginStatus.NOT_LOGGED_IN
import io.ing9990.aop.resolver.dto.RankingResultWithRank
import io.ing9990.domain.user.service.RankingService
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
    private val rankingService: RankingService,
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

        val dto: CurrentUserDto =
            userId
                ?.let { userService.findUserById(it) }
                ?.let { CurrentUserDto(it, LOGGED_IN) }
                ?: CurrentUserDto(null, NOT_LOGGED_IN)

        if (dto.loggedIn()) {
            val rank = rankingService.getUserRank(dto.currentUser?.id!!)

            val currentUserRank =
                RankingResultWithRank(
                    userId = dto.id(),
                    nickname = dto.name(),
                    bio = dto.bio(),
                    prestige = dto.prestige(),
                    rank = rank,
                    profileImage = dto.image(),
                )

            mavContainer?.model?.put("currentUserRank", currentUserRank)
        }
        return dto
    }
}
