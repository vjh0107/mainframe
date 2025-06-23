package kr.junhyung.mainframe.velocity.coroutines

import com.velocitypowered.api.plugin.PluginContainer
import kotlinx.coroutines.CoroutineExceptionHandler
import kr.junhyung.mainframe.velocity.MainframePlugin
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

@AutoConfiguration
public class VelocityCoroutinesAutoConfiguration {

    @Bean
    public fun pluginCoroutineScope(
        pluginContainer: PluginContainer,
        exceptionHandler: CoroutineExceptionHandler
    ): VelocityPluginCoroutineScope {
        return VelocityPluginCoroutineScope(pluginContainer, exceptionHandler)
    }

    @ConditionalOnMissingBean(CoroutineExceptionHandler::class)
    @Bean
    public fun coroutinesExceptionHandler(
        pluginContainer: PluginContainer
    ): CoroutineExceptionHandler {
        val instance = pluginContainer.instance.get()
        if (instance !is MainframePlugin<*>) {
            error("Plugin instance is not of type MainframePlugin")
        }
        return CoroutineExceptionHandler { context, exception ->
            instance.logger.error("An unhandled exception occurred in ${pluginContainer.description.id}, coroutine: $context", exception)
        }
    }


}