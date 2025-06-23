package kr.junhyung.mainframe.brigadier

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Role

@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration
public class CommandAutoConfiguration {

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnBean(CommandRegistrar::class)
    @Bean
    public fun commandAnnotationProcessor(
        registrar: CommandRegistrar
    ): CommandAnnotationProcessor {
        return CommandAnnotationProcessor(registrar)
    }

}