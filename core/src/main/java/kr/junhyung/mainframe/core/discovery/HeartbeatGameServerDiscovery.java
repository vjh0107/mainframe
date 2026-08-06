package kr.junhyung.mainframe.core.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.client.discovery.event.HeartbeatMonitor;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.EventListener;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartbeatGameServerDiscovery implements GameServerDiscovery, SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(HeartbeatGameServerDiscovery.class);
    private static final String THREAD_NAME = "gameserver-discovery-resync";

    private final GameServerDiscoveryClient client;
    private final Duration resyncInterval;
    private final HeartbeatMonitor monitor = new HeartbeatMonitor();

    private volatile List<GameServerServiceInstance> instances = List.of();
    private ScheduledExecutorService executor;
    private volatile boolean running;

    public HeartbeatGameServerDiscovery(GameServerDiscoveryClient client, Duration resyncInterval) {
        this.client = client;
        this.resyncInterval = resyncInterval;
    }

    @EventListener
    public void onHeartbeat(HeartbeatEvent event) {
        if (monitor.update(event.getValue())) {
            refresh();
        }
    }

    @Override
    public synchronized void start() {
        if (running) {
            return;
        }
        executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, THREAD_NAME);
            thread.setDaemon(true);
            return thread;
        });
        long delay = resyncInterval.toMillis();
        executor.scheduleWithFixedDelay(this::resync, delay, delay, TimeUnit.MILLISECONDS);
        running = true;
        log.info("Discovery resync every {}", resyncInterval);
    }

    @Override
    public synchronized void stop() {
        running = false;
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    public void refresh() {
        instances = client.getInstances();
    }

    private void resync() {
        try {
            refresh();
        } catch (Exception exception) {
            log.warn("Discovery resync failed", exception);
        }
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
