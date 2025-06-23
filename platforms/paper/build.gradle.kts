plugins {
    `java-library`
    alias(conventions.plugins.kotlin.jvm)
    alias(conventions.plugins.publishing)
}

dependencies {
    api(projects.core)

    compileOnlyApi(libs.paper.api)
}