package kr.junhyung.mainframe.platform.paper.discovery.consul;

import kr.junhyung.mainframe.core.discovery.GameServerDiscoveryProperties;
import kr.junhyung.mainframe.core.discovery.GameServerMetadata;
import org.bukkit.Bukkit;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistrationCustomizer;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

public class ConsulGameServerRegistration implements SmartLifecycle {

    private final ConsulServiceRegistry registry;
    private final ConsulAutoRegistration registration;
    private volatile boolean running;

    public ConsulGameServerRegistration(ConsulServiceRegistry registry,
                                        ConsulDiscoveryProperties discoveryProperties,
                                        HeartbeatProperties heartbeatProperties,
                                        ApplicationContext applicationContext,
                                        List<ConsulRegistrationCustomizer> registrationCustomizers,
                                        GameServerDiscoveryProperties properties) {
        AutoServiceRegistrationProperties autoServiceRegistrationProperties = new AutoServiceRegistrationProperties();
        autoServiceRegistrationProperties.setRegisterManagement(false);
        this.registry = registry;
        if (StringUtils.hasText(properties.getServiceName())) {
            discoveryProperties.setServiceName(properties.getServiceName());
        }
        if (StringUtils.hasText(properties.getInstanceId())) {
            discoveryProperties.setInstanceId(properties.getInstanceId());
        }
        discoveryProperties.setPort(Bukkit.getPort());
        this.registration = ConsulAutoRegistration.registration(autoServiceRegistrationProperties, discoveryProperties,
                applicationContext, registrationCustomizers, List.of(), heartbeatProperties);
        Map<String, String> metadata = this.registration.getService().getMeta();
        metadata.put(GameServerMetadata.GAME_SERVER, "true");
        metadata.put(GameServerMetadata.ENTRYPOINT, Boolean.toString(properties.isEntrypoint()));
    }

    @Override
    public void start() {
        registry.register(registration);
        running = true;
    }

    @Override
    public void stop() {
        if (running) {
            registry.deregister(registration);
            running = false;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
