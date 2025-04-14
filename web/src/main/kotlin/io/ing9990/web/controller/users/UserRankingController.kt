package io.ing9990.web.controller.users

import io.ing9990.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/ranking")
class UserRankingController(
    private val userService: UserService,
)
