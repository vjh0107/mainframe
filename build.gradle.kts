plugins {
    id("kr.junhyung.publishing") version "1.0.1" apply false
    `maven-publish`
}

subprojects {
    apply(plugin = "kr.junhyung.publishing")

    publishing {
        publications.withType<MavenPublication>().configureEach {
            if (!name.endsWith("PluginMarkerMaven")) {
                artifactId = "${rootProject.name}-${project.name}"
            }
        }
    }

}