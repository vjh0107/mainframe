package kr.junhyung.mainframe.paper.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import org.bukkit.plugin.Plugin
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

@AutoConfiguration
public class BukkitCoroutinesAutoConfiguration {

    @Bean
    public fun bukkitDispatchers(plugin: Plugin): BukkitDispatchers {
        return BukkitDispatchersImpl(plugin)
    }

    @Bean
    public fun pluginCoroutineScope(
        plugin: Plugin,
        exceptionHandler: CoroutineExceptionHandler
    ): BukkitPluginCoroutineScope {
        return BukkitPluginCoroutineScope(plugin, exceptionHandler)
    }

    @ConditionalOnMissingBean(CoroutineExceptionHandler::class)
    @Bean
    public fun coroutinesExceptionHandler(
        plugin: Plugin
    ): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { context, exception ->
            plugin.slF4JLogger.error("An unhandled exception occurred in ${plugin.name}, coroutine: $context", exception)
        }
    }

}