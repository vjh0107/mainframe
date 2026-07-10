package kr.junhyung.mainframe.gradle

import kr.junhyung.pluginjar.gradle.PluginJarArtifacts
import kr.junhyung.pluginjar.gradle.PluginJarBasePlugin
import kr.junhyung.pluginjar.gradle.ResolveMainClass
import kr.junhyung.pluginjar.gradle.image.PluginImageBuildPlugin
import kr.junhyung.pluginjar.gradle.nested.NestedPluginJarPlugin
import kr.junhyung.pluginjar.gradle.velocity.VelocityPluginExtension
import kr.junhyung.pluginjar.gradle.velocity.VelocityPluginManifestPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

abstract class MainframeVelocityPlugin : Plugin<Project> {

    private companion object {
        const val SPRING_BOOT_APPLICATION = "org.springframework.boot.autoconfigure.SpringBootApplication"
        const val MAINFRAME_PLUGIN = "kr.junhyung.mainframe.platform.velocity.plugin.MainframeVelocityPlugin"
    }

    override fun apply(project: Project) {
        project.plugins.apply(MainframeBasePlugin::class.java)
        project.plugins.apply(VelocityPluginManifestPlugin::class.java)
        project.plugins.apply(NestedPluginJarPlugin::class.java)
        project.plugins.apply(PluginImageBuildPlugin::class.java)
        project.plugins.withType<JavaPlugin> {
            val dependencies = project.dependencies
            dependencies.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, MainframeArtifacts.platformVelocity)
            val bootstrap = dependencies.add(
                PluginJarBasePlugin.BOOTSTRAP_CONFIGURATION_NAME,
                MainframeArtifacts.platformVelocity,
            ) as ExternalModuleDependency
            bootstrap.isTransitive = false
            dependencies.add(PluginJarBasePlugin.BOOTSTRAP_CONFIGURATION_NAME, PluginJarArtifacts.velocityLoader)

            val resolveMainClass = ResolveMainClass.register(project, SPRING_BOOT_APPLICATION)
            val resourceRoot = project.layout.buildDirectory.dir("generated/mainframe")
            resolveMainClass.configure {
                outputFile.set(resourceRoot.map { it.file("META-INF/mainframe/application-class.properties") })
            }
            project.extensions.getByType<SourceSetContainer>()
                .getByName(SourceSet.MAIN_SOURCE_SET_NAME)
                .resources.srcDir(resolveMainClass.map { resourceRoot.get().asFile })

            project.extensions.getByType<VelocityPluginExtension>().main.convention(MAINFRAME_PLUGIN)
        }
    }
}
