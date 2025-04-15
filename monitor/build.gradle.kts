dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    // Spring AOP
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // Slack API Client
    implementation("com.slack.api:slack-api-client:1.29.2")
    implementation("com.slack.api:slack-api-model:1.29.2")

    // Logback Slack Appender
    implementation("com.github.maricn:logback-slack-appender:1.6.1")

    // JSON 처리
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
