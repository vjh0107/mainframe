package kr.junhyung.mainframe.platform.paper.nametag;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

@AutoConfiguration
@ConditionalOnClass(Server.class)
public class NametagAutoConfiguration {

    @Bean
    @ConditionalOnBean({Plugin.class, Nametag.class})
    @ConditionalOnMissingBean
    NametagServiceImpl nametagService(List<Nametag> nametags, Plugin plugin) {
        return new NametagServiceImpl(nametags, plugin);
    }

    @Bean
    @ConditionalOnBean(NametagServiceImpl.class)
    @ConditionalOnMissingBean
    NametagHandler nametagHandler(NametagServiceImpl service, Plugin plugin) {
        return new NametagHandler(service, plugin);
    }

    @Bean
    @ConditionalOnBean(NametagServiceImpl.class)
    @ConditionalOnMissingBean
    NametagPassengerInterceptor nametagPassengerInterceptor(NametagServiceImpl service) {
        return new NametagPassengerInterceptor(service);
    }
}
