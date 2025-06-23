package kr.junhyung.mainframe.velocity.coroutines

import com.velocitypowered.api.plugin.PluginContainer
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kr.junhyung.mainframe.core.coroutines.GracefulShutdownCoroutineScope
import kr.junhyung.mainframe.velocity.MainframePlugin
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext

public class VelocityPluginCoroutineScope(
    private val pluginContainer: PluginContainer,
    private val exceptionHandler: CoroutineExceptionHandler
) : GracefulShutdownCoroutineScope() {

    private val pluginLogger by lazy {
        val instance = pluginContainer.instance.get()
        if (instance !is MainframePlugin<*>) {
            error("Plugin instance is not of type MainframePlugin")
        }
        instance.logger
    }

    override fun getLogger(): Logger {
        return pluginLogger
    }

    override fun coroutineContext(): CoroutineContext {
        return CoroutineName(pluginContainer.description.id) + exceptionHandler
    }

}