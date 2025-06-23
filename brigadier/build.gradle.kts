plugins {
    alias(conventions.plugins.kotlin.jvm)
    alias(conventions.plugins.publishing)
}

dependencies {
    api(projects.core)

    api(libs.brigadier)
}