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

configure(listOf(project(":core"), project(":platform-adventure"), project(":platform-paper"),
        project(":platform-velocity"))) {
    plugins.withId("java-library") {
        dependencies {
            "testImplementation"(platform(project(":bom")))
            "testImplementation"("org.springframework.boot:spring-boot-starter-test")
            "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }
}
