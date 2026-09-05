plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:${libs.versions.spring.boot.get()}"))
    api(platform("org.springframework.cloud:spring-cloud-dependencies:2025.1.2"))
    api(platform("net.kyori:adventure-bom:4.26.1"))

    constraints {
        api("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
        api("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")

        api(project(":core"))
        api(project(":platform-paper"))
        api(project(":platform-velocity"))
    }
}