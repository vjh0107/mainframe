package kr.junhyung.mainframe.paper.brigadier

import kr.junhyung.mainframe.brigadier.CommandAutoConfiguration
import kr.junhyung.mainframe.brigadier.CommandRegistrar
import kr.junhyung.mainframe.paper.condition.ConditionalOnMinecraftVersion
import org.bukkit.plugin.Plugin
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Role

@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfigureBefore(CommandAutoConfiguration::class)
@ConditionalOnMinecraftVersion("1.21.1")
@AutoConfiguration
public class PaperCommandAutoConfiguration {

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    public fun paperCommandRegistrar(
        plugin: Plugin
    ): CommandRegistrar {
        return PaperCommandRegistrar(plugin)
    }

}