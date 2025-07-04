package kr.junhyung.mainframe.convention

plugins {
    `maven-publish`
}

if (plugins.hasPlugin(JavaPlugin::class)) {
    configure<JavaPluginExtension> {
        withSourcesJar()
        withJavadocJar()
    }
}

afterEvaluate {
    publishing.publications.ifEmpty {
        publishing.publications {
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


afterEvaluate {
    publishing.repositories {
        if (project.version == "unspecified") {
            error("Project version must be specified for publishing.")
        }
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
    return project.version.toString().endsWith("-SNAPSHOT")
}