plugins {
    alias(conventions.plugins.kotlin.jvm)
    alias(conventions.plugins.publishing)
}

dependencies {
    api(projects.core)

    api(libs.exposed.core)
    api(libs.exposed.java.time)
    api(libs.exposed.json)
    api(libs.hikaricp)
    runtimeOnly(libs.exposed.jdbc)

    implementation(libs.adventure.text.serializer.gson)

    api(libs.spring.boot.starter.jdbc)
}