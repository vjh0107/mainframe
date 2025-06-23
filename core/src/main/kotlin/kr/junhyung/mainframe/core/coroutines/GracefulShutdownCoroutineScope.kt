package kr.junhyung.mainframe.core.coroutines

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import kotlin.coroutines.CoroutineContext

public abstract class GracefulShutdownCoroutineScope : CoroutineScope, DisposableBean {

    protected val supervisorJob: CompletableJob = SupervisorJob()

    public open fun getLogger(): Logger {
        return LoggerFactory.getLogger(GracefulShutdownCoroutineScope::class.java)
    }

    final override val coroutineContext: CoroutineContext
        get() = supervisorJob + coroutineContext()

    public abstract fun coroutineContext(): CoroutineContext

    final override fun destroy() {
        shutdownGracefully()
    }

    private fun shutdownGracefully(period: Long = 10_000) {
        supervisorJob.cancel()
        runBlocking {
            val result = withTimeoutOrNull(period) {
                supervisorJob.join()
            }
            if (result == null) {
                getLogger().warn("Plugin shutdown took too long, forcefully shutting down")
            }
        }
    }

}