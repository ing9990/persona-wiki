allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":notification-api"))
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation(kotlin("test"))

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")

    // Test
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2") // JUnit 5 러너
    testImplementation("io.kotest:kotest-assertions-core:5.7.2") // 기본 assertions
    testImplementation("io.kotest:kotest-property:5.7.2")
    testImplementation(kotlin("test"))
    testImplementation("com.h2database:h2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Q클래스 생성 위치 설정
kapt {
    javacOptions {
        option("querydsl.entityAccessors", true)
    }
    arguments {
        arg("querydsl.packageSuffix", ".querydsl")
    }
}
