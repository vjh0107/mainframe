package kr.junhyung.mainframe.core.adventure

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.flattener.ComponentFlattener

public fun Component.flatten(flattener: ComponentFlattener = ComponentFlattener.textOnly()): String {
    return buildString {
        flattener.flatten(this@flatten) { segment ->
            append(segment)
        }
    }
}