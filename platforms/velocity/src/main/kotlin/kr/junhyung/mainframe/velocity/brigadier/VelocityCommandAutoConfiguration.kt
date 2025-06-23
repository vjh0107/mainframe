package kr.junhyung.mainframe.velocity.brigadier

import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.proxy.ProxyServer
import kr.junhyung.mainframe.brigadier.CommandAutoConfiguration
import kr.junhyung.mainframe.brigadier.CommandRegistrar
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Role

@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfigureBefore(CommandAutoConfiguration::class)
@AutoConfiguration
public class VelocityCommandAutoConfiguration {

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    public fun velocityCommandRegistrar(
        proxyServer: ProxyServer,
        pluginContainer: PluginContainer,
    ): CommandRegistrar {
        return VelocityCommandRegistrar(proxyServer.commandManager, pluginContainer.instance.get())
    }

}