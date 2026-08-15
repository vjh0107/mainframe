package kr.junhyung.mainframe.core.discovery;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

public class GameServerDiscoveryClient {

    private final DiscoveryClient delegate;

    public GameServerDiscoveryClient(DiscoveryClient delegate) {
        this.delegate = delegate;
    }

    public List<String> getServices() {
        return delegate.getServices();
    }

    public List<GameServerServiceInstance> getInstances(String serviceId) {
        return delegate.getInstances(serviceId).stream()
                .filter(GameServerDiscoveryClient::isGameServer)
                .map(GameServerServiceInstance::new)
                .toList();
    }

    public List<GameServerServiceInstance> getInstances() {
        return delegate.getServices().stream()
                .flatMap(serviceId -> delegate.getInstances(serviceId).stream())
                .filter(GameServerDiscoveryClient::isGameServer)
                .map(GameServerServiceInstance::new)
                .toList();
    }

    private static boolean isGameServer(ServiceInstance instance) {
        Map<String, String> metadata = instance.getMetadata();
        return metadata != null && Boolean.parseBoolean(metadata.get(GameServerMetadata.GAME_SERVER));
    }
}
