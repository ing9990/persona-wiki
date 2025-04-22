dependencies {
    // AOP 어노테이션 가져와야돼서 web도 추가함.
    implementation("org.springframework.boot:spring-boot-starter-web")
    // Spring AOP
    implementation("org.springframework.boot:spring-boot-starter-aop")
    // health check actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Slack API Client
    implementation("com.slack.api:slack-api-client:1.29.2")
    implementation("com.slack.api:slack-api-model:1.29.2")

    // Logback Slack Appender
    implementation("com.github.maricn:logback-slack-appender:1.6.1")

    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.github.microutils:kotlin-logging:2.1.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // JSON 처리
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
