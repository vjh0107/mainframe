package kr.junhyung.mainframe.brigadier

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

/**
 * Annotation to mark a class as a Brigadier command.
 * This annotation provides a way to register command in lifecycle events.
 */
@Component
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
public annotation class Command(

    val name: String,

    val description: String = "",

    val aliases: Array<String> = [],

    val permission: String = "",

    val usageMessage: String = "",

    @get:AliasFor(annotation = Component::class)
    val value: String = ""

)