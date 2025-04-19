package io.ing9990.notification.email.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
@ConfigurationProperties(prefix = "notification.email")
class EmailConfig(
    val fromAddress: String = "vote4.korean@gmail.com",
    val senderName: String = "국민사형투표",
    val enabled: Boolean = true,
    val retry: RetryConfig = RetryConfig(),
) {
    data class RetryConfig(
        val maxAttempts: Int = 3,
        val initialDelay: Long = 1000,
        val multiplier: Double = 2.0,
    )

    @Bean(name = ["emailTaskExecutor"])
    fun emailTaskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 5
        executor.maxPoolSize = 10
        executor.queueCapacity = 25
        executor.setThreadNamePrefix("email-")
        return executor
    }
}
