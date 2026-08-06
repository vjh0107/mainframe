package kr.junhyung.mainframe.core.discovery;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(afterName = "org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration")
@ConditionalOnClass(DiscoveryClient.class)
@EnableConfigurationProperties(GameServerDiscoveryProperties.class)
public class GameServerDiscoveryAutoConfiguration {

    @Bean
    @ConditionalOnBean(DiscoveryClient.class)
    @ConditionalOnMissingBean
    public GameServerDiscoveryClient gameServerDiscoveryClient(DiscoveryClient discoveryClient) {
        return new GameServerDiscoveryClient(discoveryClient);
    }

    @Bean
    @ConditionalOnBean(GameServerDiscoveryClient.class)
    @ConditionalOnMissingBean
    public HeartbeatGameServerDiscovery gameServerDiscovery(GameServerDiscoveryClient client,
                                                            GameServerDiscoveryProperties properties) {
        return new HeartbeatGameServerDiscovery(client, properties.getResyncInterval());
    }
}
