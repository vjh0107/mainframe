package kr.junhyung.mainframe.paper.condition

import org.springframework.context.annotation.Conditional

/**
 * e.g. `@ConditionalOnMinecraftVersion("1.20.1")`
 */
@Conditional(OnMinecraftVersionCondition::class)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
public annotation class ConditionalOnMinecraftVersion(val version: String)