package kr.junhyung.mainframe.convention

import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.maven

plugins {
    `maven-publish`
}

if (plugins.hasPlugin(JavaPlugin::class)) {
    configure<JavaPluginExtension> {
        withSourcesJar()
        withJavadocJar()
    }
}

configure<PublishingExtension> {
    afterEvaluate {
        publications.ifEmpty {
            publications {
                create<MavenPublication>("maven") {
                    groupId = project.group.toString()
                    version = project.version.toString()
                    artifactId = project.rootProject.name + "-" + project.name

                    if (plugins.hasPlugin(JavaPlugin::class)) {
                        from(components["java"])
                    }

                    if (plugins.hasPlugin(JavaPlatformPlugin::class)) {
                        from(components["javaPlatform"])
                    }
                }
            }
        }
    }

    repositories {
        val url = if (isSnapshot()) {
            "https://nexus.junhyung.kr/repository/maven-snapshots/"
        } else {
            "https://nexus.junhyung.kr/repository/maven-releases/"
        }
        maven(url) {
            credentials {
                username = System.getenv("NEXUS_USERNAME").toString()
                password = System.getenv("NEXUS_PASSWORD").toString()
            }
        }
    }
}

fun isSnapshot(): Boolean {
    return project.version.toString().endsWith("SNAPSHOT")
}