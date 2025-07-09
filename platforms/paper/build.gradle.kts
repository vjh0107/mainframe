plugins {
    `java-library`
    alias(conventions.plugins.kotlin.jvm)
    alias(conventions.plugins.publishing)
}

dependencies {
    api(projects.core)

    compileOnlyApi(libs.paper.api)

    testImplementation(libs.paper.api)
    testImplementation(libs.spring.boot.starter.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}