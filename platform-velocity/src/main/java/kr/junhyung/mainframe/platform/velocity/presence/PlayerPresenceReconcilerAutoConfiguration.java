package kr.junhyung.mainframe.platform.velocity.presence;

import com.velocitypowered.api.proxy.ProxyServer;
import kr.junhyung.mainframe.core.presence.PlayerPresenceCommandService;
import kr.junhyung.mainframe.core.presence.PlayerPresenceProperties;
import kr.junhyung.mainframe.core.presence.redis.RedisPlayerPresenceAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = RedisPlayerPresenceAutoConfiguration.class)
@ConditionalOnClass(ProxyServer.class)
public class PlayerPresenceReconcilerAutoConfiguration {

    @Bean
    @ConditionalOnBean({ProxyServer.class, PlayerPresenceCommandService.class})
    @ConditionalOnMissingBean
    public PlayerPresenceReconciler playerPresencePublisher(ProxyServer proxyServer,
                                                           PlayerPresenceCommandService presence,
                                                           PlayerPresenceProperties properties) {
        return new PlayerPresenceReconciler(proxyServer, presence, properties.getResyncInterval());
    }
}
