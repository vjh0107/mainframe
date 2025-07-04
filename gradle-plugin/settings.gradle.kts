rootProject.name = "mainframe-gradle-plugin"

includeBuild("../build-logic")

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        maven("https://junhyung.nexus/")
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }

        create("conventions") {
            file("../build-logic/convention/src/main/kotlin/kr/junhyung/mainframe/convention").listFiles {
                    file -> file.name.endsWith(".gradle.kts")
            }!!.forEach { file ->
                val pluginName = file.name.removeSuffix(".gradle.kts")
                plugin(pluginName.removeSuffix("-convention"), "kr.junhyung.mainframe.convention.$pluginName").version("")
            }
        }
    }
}