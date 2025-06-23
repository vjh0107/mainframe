package kr.junhyung.mainframe.core.event

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

@Component
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
public annotation class Listener(

    @get:AliasFor(annotation = Component::class)
    val value: String = ""

)