package kr.junhyung.mainframe.core.util

public object ClassLoaderUtils {

    public fun withClassLoader(
        classLoader: ClassLoader,
        block: () -> Unit
    ) {
        val originalClassLoader = Thread.currentThread().contextClassLoader
        try {
            Thread.currentThread().contextClassLoader = classLoader
            block()
        } finally {
            Thread.currentThread().contextClassLoader = originalClassLoader
        }
    }

}