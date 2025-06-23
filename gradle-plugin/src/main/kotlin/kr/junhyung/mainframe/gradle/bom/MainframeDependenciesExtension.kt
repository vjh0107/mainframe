package kr.junhyung.mainframe.gradle.bom

import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.create

public abstract class MainframeDependenciesExtension(
    private val dependencyHandler: DependencyHandler,
) {

    private companion object {
        const val MAINFRAME_GROUP = "kr.junhyung.mainframe"

        const val MAINFRAME_CORE = "mainframe-core"
        const val MAINFRAME_EXPOSED = "mainframe-exposed"

        const val MAINFRAME_PAPER = "mainframe-paper"
        const val MAINFRAME_PAPER_BRIGADIER = "mainframe-paper-brigadier"
        const val MAINFRAME_VELOCITY = "mainframe-velocity"
    }

    public val core: ExternalModuleDependency get() {
        return dependencyHandler.create(MAINFRAME_GROUP, MAINFRAME_CORE)
    }

    public val exposed: ExternalModuleDependency get() {
        return dependencyHandler.create(MAINFRAME_GROUP, MAINFRAME_EXPOSED)
    }

    public val paper: ExternalModuleDependency get() {
        return dependencyHandler.create(MAINFRAME_GROUP, MAINFRAME_PAPER)
    }

    public val paperBrigadier: ExternalModuleDependency get() {
        return dependencyHandler.create(MAINFRAME_GROUP, MAINFRAME_PAPER_BRIGADIER)
    }

    public val velocity: ExternalModuleDependency get() {
        return dependencyHandler.create(MAINFRAME_GROUP, MAINFRAME_VELOCITY)
    }

}