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

if (libs.versions.kotlin.get() != embeddedKotlinVersion) {
    error("Kotlin version mismatch: ${libs.versions.kotlin.get()} != $embeddedKotlinVersion")
}