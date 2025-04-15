package io.ing9990.monitor.aop

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration
@EnableAspectJAutoProxy
class LoggingConfig {
    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper().registerKotlinModule()
}
