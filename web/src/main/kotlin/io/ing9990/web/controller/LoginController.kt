package io.ing9990.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * 로그인 및 회원가입 관련 페이지를 제공하는 컨트롤러
 */
@Controller
class LoginController {

    /**
     * 소셜 로그인 페이지를 반환합니다.
     * 실제 소셜 로그인 처리는 향후 Spring Security OAuth2 Client에서 담당할 예정입니다.
     */
    @GetMapping("/login")
    fun loginPage(): String {

        return "login/login"
    }
}