package kr.junhyung.mainframe.paper

import org.bukkit.plugin.Plugin
import org.slf4j.Logger
import org.springframework.beans.factory.NoUniqueBeanDefinitionException
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Role

@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration
public class PaperPluginAutoConfiguration {

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(Plugin::class)
    @Bean(destroyMethod = "")
    public fun plugin(): Plugin {
        throw NoUniqueBeanDefinitionException(
            Plugin::class.java,
            "Plugin bean is not available. Please make sure you are using MainframePlugin."
        )
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    public fun logger(plugin: Plugin): Logger {
        return plugin.slF4JLogger
    }

}