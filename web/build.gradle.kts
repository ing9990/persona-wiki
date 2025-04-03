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
    implementation(project(":storage-api"))
    implementation(project(":admin"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
}
