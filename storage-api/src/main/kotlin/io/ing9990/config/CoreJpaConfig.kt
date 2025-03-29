package io.ing9990.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = ["io.ing9990.domain"])
@EnableJpaRepositories(basePackages = ["io.ing9990.domain"])
internal class CoreJpaConfig
