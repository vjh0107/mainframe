package kr.junhyung.mainframe.gradle.shadow

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import kr.junhyung.mainframe.gradle.MainframeExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

public abstract class MainframeShadowPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.afterEvaluate(::applyAfterEvaluate)
    }

    private fun applyAfterEvaluate(target: Project) {
        val extension = target.extensions.getByType<MainframeExtension>()
        if (!extension.enableShadowJar.get()) {
            return
        }
        target.pluginManager.apply(ShadowPlugin::class.java)
        target.tasks.withType(ShadowJar::class.java).configureEach {
            mergeServiceFiles {
                setPath("META-INF/spring")
            }
        }
    }

}