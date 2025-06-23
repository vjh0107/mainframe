package kr.junhyung.mainframe.paper.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kr.junhyung.mainframe.core.coroutines.GracefulShutdownCoroutineScope
import org.bukkit.plugin.Plugin
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext

public class BukkitPluginCoroutineScope(
    private val plugin: Plugin,
    private val exceptionHandler: CoroutineExceptionHandler
) : GracefulShutdownCoroutineScope() {

    override fun getLogger(): Logger {
        return plugin.slF4JLogger
    }

    override fun coroutineContext(): CoroutineContext {
        return CoroutineName(plugin.name) + exceptionHandler
    }

}