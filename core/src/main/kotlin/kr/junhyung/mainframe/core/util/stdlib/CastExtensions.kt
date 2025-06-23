package kr.junhyung.mainframe.core.util.stdlib

@Suppress("UNCHECKED_CAST")
public fun <T> Any.uncheckedCast(): T {
    return this as T
}

@Suppress("UNCHECKED_CAST")
public fun <T> Any.uncheckedCastOrNull(): T? {
    return this as? T
}