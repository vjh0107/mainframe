plugins {
    `java-library`
    alias(conventions.plugins.kotlin.jvm)
    alias(conventions.plugins.publishing)
}

dependencies {
    api(libs.spring.boot.starter)
    api(libs.spring.boot.starter.json)

    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.reactive)
    api(libs.kotlinx.coroutines.reactor)

    compileOnlyApi(libs.adventure.api)

    implementation(kotlin("reflect"))
}