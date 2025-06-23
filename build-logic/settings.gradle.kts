include(":convention")

pluginManagement {
    repositories {
        maven("https://junhyung.nexus/")
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        maven("https://junhyung.nexus/")
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"