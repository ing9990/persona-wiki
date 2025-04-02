package io.ing9990.web.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class WebSecurityConfig {
    @Bean
    @Order(2) // admin SecurityConfig 다음 우선순위
    fun webSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/**") // 모든 경로에 적용
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(
                        "/",
                        "/login",
                        "/auth/**",
                        "/figures",
                        "/categories/**",
                        "/*/search/**",
                        "/api/**",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/error/**",
                        "/favicon.ico",
                    ).permitAll()
                    .requestMatchers(
                        "/add-figure",
                        "/*/comment/**",
                        "/*/vote/**",
                    ).authenticated()
            }
            .formLogin { login ->
                login
                    .loginPage("/login")
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutUrl("/auth/logout")
                    .logoutSuccessUrl("/?logout=true")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
            }
            .csrf { csrf -> csrf.disable() }

        return http.build()
    }
}
