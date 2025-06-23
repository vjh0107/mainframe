plugins {
    `java-library`
    alias(conventions.plugins.kotlin.jvm)
    alias(conventions.plugins.publishing)
}

dependencies {
    api(projects.core)
    api(projects.paper)
    api(projects.brigadier)

    compileOnlyApi(libs.paper.api.v21)
}

kotlin {
    jvmToolchain(21)
}