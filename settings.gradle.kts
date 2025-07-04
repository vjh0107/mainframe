rootProject.name = "mainframe"

includeBuild("build-logic")
includeBuild("gradle-plugin")

include(":bom")

include(":core")
include(":exposed")
include(":brigadier")

include(":paper", file("platforms/paper"))
include(":paper-brigadier", file("platforms/paper-brigadier"))
include(":velocity", file("platforms/velocity"))

fun include(name: String, path: File) {
    include(name)
    project(name).projectDir = path
}

pluginManagement {
    repositories {
        maven("https://junhyung.nexus/")
    }
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://junhyung.nexus/")
    }

    versionCatalogs {
        create("conventions") {
            file("build-logic/convention/src/main/kotlin/kr/junhyung/mainframe/convention").listFiles {
                    file -> file.name.endsWith(".gradle.kts")
            }!!.forEach { file ->
                val pluginName = file.name.removeSuffix(".gradle.kts")
                plugin(pluginName.removeSuffix("-convention"), "kr.junhyung.mainframe.convention.$pluginName").version("")
            }
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")