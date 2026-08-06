package kr.junhyung.mainframe.platform.velocity.discovery;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import kr.junhyung.mainframe.core.discovery.GameServerDiscovery;
import kr.junhyung.mainframe.core.discovery.GameServerServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.EventListener;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameServerRegistrationReconciler implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(GameServerRegistrationReconciler.class);
    private static final String THREAD_NAME = "gameserver-registration-resync";

    private final ProxyServer proxyServer;
    private final GameServerDiscovery discovery;
    private final Duration resyncInterval;

    private ScheduledExecutorService executor;
    private volatile boolean running;

    public GameServerRegistrationReconciler(ProxyServer proxyServer, GameServerDiscovery discovery,
                                            Duration resyncInterval) {
        this.proxyServer = proxyServer;
        this.discovery = discovery;
        this.resyncInterval = resyncInterval;
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
        log.info("Backend registration resync every {}", resyncInterval);
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

    private void resync() {
        try {
            reconcile();
        } catch (Exception exception) {
            log.warn("Backend registration resync failed", exception);
        }
    }

    @EventListener(HeartbeatEvent.class)
    public synchronized void reconcile() {
        Map<String, ServerInfo> desired = new HashMap<>();
        for (GameServerServiceInstance instance : discovery.getInstances()) {
            String instanceId = instance.getInstanceId();
            if (instanceId == null) {
                log.warn("Instance ID is null for {}", instance);
                continue;
            }
            desired.put(instanceId, new ServerInfo(instanceId, new InetSocketAddress(instance.getHost(), instance.getPort())));
        }

        desired.forEach((instanceId, info) -> {
            Optional<RegisteredServer> existing = proxyServer.getServer(instanceId);
            if (existing.isEmpty()) {
                proxyServer.registerServer(info);
                log.info("Registered backend {} at {}", instanceId, info.getAddress());
            } else if (!existing.get().getServerInfo().getAddress().equals(info.getAddress())) {
                proxyServer.unregisterServer(existing.get().getServerInfo());
                proxyServer.registerServer(info);
                log.info("Re-registered backend {} at {}", instanceId, info.getAddress());
            }
        });

        for (RegisteredServer registered : List.copyOf(proxyServer.getAllServers())) {
            String instanceId = registered.getServerInfo().getName();
            if (!desired.containsKey(instanceId)) {
                proxyServer.unregisterServer(registered.getServerInfo());
                log.info("Unregistered backend {}", instanceId);
            }
        }
    }
}
