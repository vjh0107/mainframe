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
        val paperVersion = "26.1.2.build.70-stable"
        api("io.papermc.paper:paper-api:$paperVersion")
        api("kr.junhyung.papermc:paper-impl:$paperVersion")

        api("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")

        api(project(":core"))
        api(project(":platform-paper"))
        api(project(":platform-velocity"))
    }
}