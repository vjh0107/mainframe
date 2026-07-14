rootProject.name = "mainframe"

include(":bom")
include(":core")
include(":gradle-plugin")
include(":platform-adventure")
include(":platform-paper")
include(":platform-velocity")

pluginManagement {
    repositories {
        maven("https://junhyung.nexus/")
    }

    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://junhyung.nexus/")
    }

    versionCatalogs {
        create("libs") {
            version("spring-boot", "4.1.0")
        }
    }
}