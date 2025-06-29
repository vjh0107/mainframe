package kr.junhyung.mainframe.velocity

import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.proxy.ProxyServer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.slf4j.Logger
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Role

@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration
public class VelocityPluginAutoConfiguration {

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(destroyMethod = "")
    public fun proxyServer(velocityPluginApplicationContext: VelocityPluginApplicationContext): ProxyServer {
        return velocityPluginApplicationContext.proxyServer
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(destroyMethod = "")
    public fun pluginContainer(velocityPluginApplicationContext: VelocityPluginApplicationContext): PluginContainer {
        return velocityPluginApplicationContext.pluginContainer
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    public fun logger(pluginContainer: PluginContainer): Logger {
        val instance = pluginContainer.instance.get()
        if (instance !is VelocityMainframePlugin<*>) {
            error("Plugin instance is not a MainframePlugin. Please ensure your plugin extends MainframePlugin.")
        }
        return instance.logger
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    public fun pluginCoroutineDispatcher(pluginContainer: PluginContainer): CoroutineDispatcher {
        return pluginContainer.executorService.asCoroutineDispatcher()
    }

}