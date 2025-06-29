plugins {
    `java-platform`
    alias(conventions.plugins.publishing)
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform(libs.kotlin.bom))
    api(platform(libs.spring.boot.dependencies))
    api(platform(libs.exposed.bom))
    api(platform(libs.adventure.bom))

    constraints {
        api(libs.mysql.connector)

        api(projects.core)
        api(projects.exposed)
        api(projects.paper)
        api(projects.paperBrigadier)
        api(projects.velocity)
    }

}