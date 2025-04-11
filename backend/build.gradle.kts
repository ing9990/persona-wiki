dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    implementation(project(":monitor"))
    implementation(project(":common"))
    implementation(project(":google-image-api"))
    implementation(project(":wikipedia-api"))
    implementation(project(":storage-api"))
    implementation(project(":authentication"))

    testImplementation(kotlin("test"))
}
