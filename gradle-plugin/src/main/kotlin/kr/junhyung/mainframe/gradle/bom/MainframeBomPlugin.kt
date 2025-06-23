package kr.junhyung.mainframe.gradle.bom

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType

public abstract class MainframeBomPlugin : Plugin<Project> {

    private companion object {
        const val MAINFRAME_BOM_GROUP: String = "kr.junhyung.mainframe"
        const val MAINFRAME_BOM_NAME: String = "mainframe-bom"

        const val MAINFRAME_DEPENDENCIES_EXTENSION_NAME: String = "mainframe"
    }

    override fun apply(target: Project) {
        applyPlugin(target.pluginManager)
        configureBom(target)
        configureDependenciesExtension(target)
    }

    private fun applyPlugin(pluginManager: PluginManager) {
        pluginManager.apply(DependencyManagementPlugin::class)
    }

    private fun configureBom(target: Project) {
        val bomCoordinates = "$MAINFRAME_BOM_GROUP:$MAINFRAME_BOM_NAME:${getBomVersion()}"
        target.extensions.getByType(DependencyManagementExtension::class)
            .imports {
                mavenBom(bomCoordinates)
            }
    }

    private fun configureDependenciesExtension(target: Project) {
        target.dependencies.extensions.create<MainframeDependenciesExtension>(
            MAINFRAME_DEPENDENCIES_EXTENSION_NAME,
            target.dependencies
        )
    }

    private fun getBomVersion(): String {
        return this::class.java.`package`.implementationVersion ?: error("'implementation-version' not found")
    }

}