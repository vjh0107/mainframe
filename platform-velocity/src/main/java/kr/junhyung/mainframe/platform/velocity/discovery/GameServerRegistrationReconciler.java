package kr.junhyung.mainframe.platform.velocity.discovery;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import kr.junhyung.mainframe.core.discovery.GameServerDiscovery;
import kr.junhyung.mainframe.core.discovery.GameServerServiceInstance;
import kr.junhyung.mainframe.core.scheduling.AbstractScheduledLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.event.EventListener;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GameServerRegistrationReconciler extends AbstractScheduledLifecycle {

    private static final Logger log = LoggerFactory.getLogger(GameServerRegistrationReconciler.class);
    private final ProxyServer proxyServer;
    private final GameServerDiscovery discovery;

    public GameServerRegistrationReconciler(ProxyServer proxyServer, GameServerDiscovery discovery,
                                            Duration resyncInterval) {
        super("gameserver-registration-resync", resyncInterval);
        this.proxyServer = proxyServer;
        this.discovery = discovery;
    }


    @Override
    protected void runOnce() {
        reconcile();
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
