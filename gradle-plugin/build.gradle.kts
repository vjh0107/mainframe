import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven

plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation("kr.junhyung.pluginjar:gradle-plugin:1.3.0-SNAPSHOT")
}

gradlePlugin {
    plugins {
        register("mainframe") {
            id = "kr.junhyung.mainframe"
            implementationClass = "kr.junhyung.mainframe.gradle.MainframeBasePlugin"
        }
        register("mainframe-paper") {
            id = "kr.junhyung.mainframe.paper"
            implementationClass = "kr.junhyung.mainframe.gradle.MainframePaperPlugin"
        }
        register("mainframe-velocity") {
            id = "kr.junhyung.mainframe.velocity"
            implementationClass = "kr.junhyung.mainframe.gradle.MainframeVelocityPlugin"
        }
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to project.group,
        )
    }
}

tasks.withType<AbstractPublishToMaven>().configureEach {
    if (name.startsWith("publishMavenPublication")) {
        enabled = false
    }
}
