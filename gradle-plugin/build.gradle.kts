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

gradlePlugin {
    website = property("project.url").toString()
    vcsUrl = property("project.url.scm").toString()
    plugins {
        register("mainframe") {
            id = "kr.junhyung.mainframe"
            displayName = property("project.name").toString()
            description = property("project.description").toString()
            implementationClass = "kr.junhyung.mainframe.gradle.MainframePlugin"
            tags.set(listOf("mainframe"))
        }
    }
}