package kr.junhyung.mainframe.core.discovery;

import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.client.discovery.event.HeartbeatMonitor;
import org.springframework.context.event.EventListener;

import java.util.List;

public class HeartbeatGameServerDiscovery implements GameServerDiscovery {

    private final GameServerDiscoveryClient client;
    private final HeartbeatMonitor monitor = new HeartbeatMonitor();

    private volatile List<GameServerServiceInstance> instances = List.of();

    public HeartbeatGameServerDiscovery(GameServerDiscoveryClient client) {
        this.client = client;
    }

    @EventListener
    public void onHeartbeat(HeartbeatEvent event) {
        if (monitor.update(event.getValue())) {
            refresh();
        }
    }

    public void refresh() {
        instances = client.getInstances();
    }

    @Override
    public List<GameServerServiceInstance> getInstances() {
        return instances;
    }

    @Override
    public List<GameServerServiceInstance> getInstances(String serviceId) {
        return instances.stream()
                .filter(instance -> instance.getServiceId().equals(serviceId))
                .toList();
    }
}
