package kr.junhyung.mainframe.paper.condition

import org.springframework.context.annotation.Conditional

@Conditional(OnPluginCondition::class)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
public annotation class ConditionalOnPlugin(val plugin: String)