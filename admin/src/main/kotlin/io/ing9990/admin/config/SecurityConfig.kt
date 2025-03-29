package io.ing9990.admin.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService {
        val userDetails =
            User.builder()
                .username("persona_admin")
                .password(passwordEncoder.encode("rlaxodn1234"))
                .roles("ADMIN")
                .build()

        return InMemoryUserDetailsManager(userDetails)
    }

    @Bean
    @Order(1) // 먼저 적용되는 보안 필터
    fun adminSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/persona-admin/**") // persona-admin/** 경로에만 적용
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(
                        "/persona-admin/login",
                        "/persona-admin/css/**",
                        "/persona-admin/js/**",
                        "/persona-admin/images/**",
                    ).permitAll()
                    .anyRequest().hasRole("ADMIN")
            }
            .formLogin { form ->
                form
                    .loginPage("/persona-admin/login")
                    .loginProcessingUrl("/persona-admin/login-process")
                    .defaultSuccessUrl("/persona-admin", true)
                    .failureUrl("/persona-admin/login?error=true")
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutUrl("/persona-admin/logout")
                    .logoutSuccessUrl("/persona-admin/login?logout=true")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            }
            .csrf { csrf -> csrf.disable() }

        return http.build()
    }

    @Bean
    @Order(2) // 나중에 적용되는 보안 필터
    fun publicSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/**") // 모든 경로에 적용
            .authorizeHttpRequests { authorize ->
                authorize.anyRequest().permitAll()
            }
            .csrf { csrf -> csrf.disable() }

        return http.build()
    }
}
