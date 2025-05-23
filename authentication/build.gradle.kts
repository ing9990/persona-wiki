dependencies {

    implementation(project(":common"))
    implementation(project(":storage-api"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation(kotlin("test"))
}
