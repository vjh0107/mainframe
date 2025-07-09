plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.plugin.spring) apply false
}

allprojects {
    group = extra["project.group"].toString()
    version = extra["project.version"].toString()
    description = extra["project.description"].toString()
}

subprojects {

    if (plugins.hasPlugin(JavaPlugin::class)) {
        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
}

tasks.register("publish") {
    val gradlePluginProject = gradle.includedBuild("gradle-plugin")
    dependsOn(gradlePluginProject.task(":publish"))
    subprojects.forEach { subproject ->
        val publishTask = subproject.tasks.findByName("publish")
        if (publishTask != null) {
            dependsOn(publishTask)
        }
    }
}