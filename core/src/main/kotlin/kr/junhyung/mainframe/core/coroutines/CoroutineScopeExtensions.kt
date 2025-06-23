package kr.junhyung.mainframe.core.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future

public fun CoroutineScope.block(block: suspend CoroutineScope.() -> Unit) {
    this.future {
        try {
            block()
        } catch (throwable: Throwable) {
            val exceptionHandler = coroutineContext[CoroutineExceptionHandler]
            if (exceptionHandler != null) {
                exceptionHandler.handleException(coroutineContext, throwable)
            } else {
                throw throwable
            }
        }
    }.join()
}