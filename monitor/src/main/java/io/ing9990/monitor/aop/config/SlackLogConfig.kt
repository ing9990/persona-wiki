package io.ing9990.monitor.aop.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class SlackLoggerConfig {
    @Bean
    fun slackLoggerProperties(): SlackLoggerProperties = SlackLoggerProperties()
}

@Component
@ConfigurationProperties(prefix = "slack.webhook")
class SlackLoggerProperties {
    var url: String =
        "https://hooks.slack.com/services/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"
    var channel: String = "#api-monitoring"
    var username: String = "국민사형투표"
}
