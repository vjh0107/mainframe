package kr.junhyung.mainframe.platform.paper.discovery.consul;

import kr.junhyung.mainframe.core.discovery.GameServerDiscoveryProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistrationCustomizer;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;

@AutoConfiguration
@ConditionalOnConsulEnabled
@EnableConfigurationProperties(GameServerDiscoveryProperties.class)
public class ConsulGameServerRegistrationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ConsulGameServerTcpHealthCheckCustomizer consulGameServerTcpHealthCheckCustomizer(ConsulDiscoveryProperties properties) {
        return new ConsulGameServerTcpHealthCheckCustomizer(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConsulGameServerRegistration consulGameServerRegistration(ConsulServiceRegistry registry,
                                                                     ConsulDiscoveryProperties discoveryProperties,
                                                                     HeartbeatProperties heartbeatProperties,
                                                                     ApplicationContext applicationContext,
                                                                     List<ConsulRegistrationCustomizer> registrationCustomizers,
                                                                     GameServerDiscoveryProperties properties) {
        return new ConsulGameServerRegistration(registry, discoveryProperties, heartbeatProperties, applicationContext,
                registrationCustomizers, properties);
    }
}
