package io.ing9990.web.controller.advice

import io.ing9990.aop.CurrentUser
import io.ing9990.aop.resolver.CurrentUserDto
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice
class GlobalModelAdvice {
    @ModelAttribute("current")
    fun addCurrentUser(
        @CurrentUser currentUserDto: CurrentUserDto,
    ): CurrentUserDto = currentUserDto
}
