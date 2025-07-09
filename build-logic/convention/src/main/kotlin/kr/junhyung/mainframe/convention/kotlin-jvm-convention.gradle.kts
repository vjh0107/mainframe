package kr.junhyung.mainframe.convention

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    kotlin("jvm")
}

runCatching { // Apply the Kotlin Spring plugin if available
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
}

configure<KotlinJvmProjectExtension> {
    jvmToolchain(17)

    explicitApi = ExplicitApiMode.Strict
    compilerOptions {
        allWarningsAsErrors.set(true)
    }
}

afterEvaluate {
    tasks.withType<Jar> {
        manifest {
            attributes(
                "Implementation-Version" to project.version
            )
        }
    }
}