package io.ing9990.admin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan(basePackages = ["io.ing9990.domain"]) // storage-api 모듈의 엔티티 스캔
@EnableJpaRepositories(basePackages = ["io.ing9990.admin.repository"]) // Admin 모듈의 리포지토리 스캔
@ComponentScan(basePackages = ["io.ing9990.admin"]) // Admin 모듈의 컴포넌트 스캔
class AdminApplication

fun main(args: Array<String>) {
    runApplication<AdminApplication>(*args)
}
