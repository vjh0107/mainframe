package kr.junhyung.mainframe.exposed.table

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
public annotation class Table(
    @get:AliasFor(annotation = Component::class)
    val value: String = ""
)