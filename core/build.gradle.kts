plugins {
    `java-library`
}

dependencies {
    api(platform(project(":bom")))

    api("org.springframework.boot:spring-boot-starter")
}