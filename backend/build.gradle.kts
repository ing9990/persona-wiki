dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":monitor"))
    implementation(project(":storage-api"))

    testImplementation(kotlin("test"))
}
