import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    `kotlin-dsl`
    alias(libs.plugins.plugin.publish)
    alias(conventions.plugins.kotlin.jvm)
    alias(conventions.plugins.publishing)
}

dependencies {
    implementation(libs.shadow)
    implementation(libs.spring.dependency.management)
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("allopen"))
}

val rootProperties = loadProperties(rootProject.gradle.parent?.rootProject?.projectDir?.path + "/gradle.properties")

group = rootProperties["project.group"].toString()
version = rootProperties["project.version"].toString()
description = rootProperties["project.description"].toString()

gradlePlugin {
    website = rootProperties["project.url"].toString()
    vcsUrl = rootProperties["project.url.scm"].toString()
    plugins {
        register("mainframe") {
            id = "kr.junhyung.mainframe"
            displayName = rootProperties["project.name"].toString()
            description = rootProperties["project.description"].toString()
            implementationClass = "kr.junhyung.mainframe.gradle.MainframePlugin"
            tags.set(listOf("mainframe"))
        }
    }
}