plugins {
    `java-library`
}

dependencies {
    api(platform(project(":bom")))

    api("org.springframework.boot:spring-boot-starter")
    compileOnly("org.springframework.cloud:spring-cloud-commons")
    compileOnly("org.springframework.boot:spring-boot-starter-data-redis")

    testImplementation("org.springframework.boot:spring-boot-starter-data-redis")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("com.redis:testcontainers-redis")
}
