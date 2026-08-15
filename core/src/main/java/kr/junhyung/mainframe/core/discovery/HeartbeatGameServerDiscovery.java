package kr.junhyung.mainframe.core.discovery;

import kr.junhyung.mainframe.core.scheduling.AbstractScheduledLifecycle;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.client.discovery.event.HeartbeatMonitor;
import org.springframework.context.event.EventListener;

import java.time.Duration;
import java.util.List;

public class HeartbeatGameServerDiscovery extends AbstractScheduledLifecycle implements GameServerDiscovery {

    private final GameServerDiscoveryClient client;
    private final HeartbeatMonitor monitor = new HeartbeatMonitor();

    private volatile List<GameServerServiceInstance> instances = List.of();

    public HeartbeatGameServerDiscovery(GameServerDiscoveryClient client, Duration resyncInterval) {
        super("gameserver-discovery-resync", resyncInterval);
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
    protected Duration initialDelay() {
        return Duration.ZERO;
    }

    @Override
    protected void runOnce() {
        refresh();
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
