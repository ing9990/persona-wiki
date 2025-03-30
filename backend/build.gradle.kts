dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation(project(":monitor"))
    implementation(project(":storage-api"))

    testImplementation(kotlin("test"))
}
