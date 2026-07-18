package kr.junhyung.mainframe.platform.velocity.discovery;

import com.velocitypowered.api.proxy.ProxyServer;
import kr.junhyung.mainframe.core.discovery.GameServerDiscoveryReconciler;
import kr.junhyung.mainframe.core.discovery.GameServerDiscovery;
import kr.junhyung.mainframe.core.discovery.GameServerDiscoveryProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass({ProxyServer.class, HeartbeatEvent.class})
public class GameServerRegistrationReconcilerAutoConfiguration {

    @Bean
    @ConditionalOnBean({ProxyServer.class, GameServerDiscovery.class})
    @ConditionalOnMissingBean
    public GameServerRegistrationReconciler gameServerRegistrationReconciler(ProxyServer proxyServer,
                                                                             GameServerDiscovery discovery) {
        return new GameServerRegistrationReconciler(proxyServer, discovery);
    }

    @Bean
    @ConditionalOnBean(GameServerRegistrationReconciler.class)
    public GameServerDiscoveryReconciler gameServerRegistrationResync(GameServerRegistrationReconciler reconciler,
                                                                      GameServerDiscoveryProperties properties) {
        return new GameServerDiscoveryReconciler("gameserver-registration-resync", properties.getResyncInterval(), reconciler::reconcile);
    }

}
