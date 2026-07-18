package kr.junhyung.mainframe.platform.velocity.discovery;

import com.velocitypowered.api.proxy.ProxyServer;
import kr.junhyung.mainframe.core.discovery.GameServerDiscovery;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(ProxyServer.class)
public class GameServerRoutingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GameServerLoadBalancer gameServerLoadBalancer() {
        return new P2CGameServerLoadBalancer();
    }

    @Bean
    @ConditionalOnBean({ProxyServer.class, GameServerDiscovery.class})
    @ConditionalOnMissingBean
    public GameServerConnectionRouter gameServerConnectionRouter(ProxyServer proxyServer, GameServerDiscovery discovery,
                                                                 GameServerLoadBalancer loadBalancer) {
        return new GameServerConnectionRouter(proxyServer, discovery, loadBalancer);
    }
}
