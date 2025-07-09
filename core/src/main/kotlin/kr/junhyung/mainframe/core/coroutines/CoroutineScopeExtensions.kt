package kr.junhyung.mainframe.core.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.future
import kotlinx.coroutines.launch

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

public fun CoroutineScope.fireAndForgetScope(): CoroutineScope {
    return CoroutineScope(coroutineContext + SupervisorJob())
}

public fun CoroutineScope.launchAndForget(
    block: suspend CoroutineScope.() -> Unit
): Job {
    return fireAndForgetScope().launch(block = block)
}