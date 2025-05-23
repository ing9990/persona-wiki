tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}

dependencies {
    implementation(project(":authentication"))
    implementation(project(":backend"))
    implementation(project(":common"))
    implementation(project(":monitor"))
    implementation(project(":notification-api"))
    implementation(project(":storage-api"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
    implementation("org.springframework.boot:spring-boot-devtools")

    // JSON 처리
//    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
