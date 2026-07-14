package kr.junhyung.mainframe.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.withType

abstract class MainframeBasePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.withType<JavaPlugin> {
            val dependencies = project.dependencies
            dependencies.add(
                JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
                dependencies.platform(MainframeArtifacts.bom),
            )
        }
    }
}
