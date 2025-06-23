package kr.junhyung.mainframe.gradle

import kr.junhyung.mainframe.gradle.bom.MainframeBomPlugin
import kr.junhyung.mainframe.gradle.shadow.MainframeShadowPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.allopen.gradle.SpringGradleSubplugin
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

public abstract class MainframePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        configureKotlin(target)

        createExtension(target)

        target.pluginManager.apply(MainframeBomPlugin::class)
        target.pluginManager.apply(MainframeShadowPlugin::class)
    }

    private fun configureKotlin(target: Project) {
        target.pluginManager.apply(KotlinPluginWrapper::class)
        target.pluginManager.apply(SpringGradleSubplugin::class)
        val javaVersion = target.extensions.getByType<JavaPluginExtension>().toolchain.languageVersion
        if (javaVersion.isPresent) {
            target.configure<KotlinProjectExtension> {
                jvmToolchain {
                    languageVersion.set(javaVersion.get())
                }
            }
        }
    }

    private fun createExtension(target: Project) {
        target.extensions.create<MainframeExtension>(MainframeExtension.NAME)
    }

}