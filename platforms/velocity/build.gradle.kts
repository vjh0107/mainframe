plugins {
    `java-library`
    alias(conventions.plugins.kotlin.jvm)
    alias(conventions.plugins.publishing)
}

dependencies {
    api(projects.core)
    api(projects.brigadier)

    compileOnlyApi(libs.velocity.api)
    implementation(kotlin("reflect"))
}