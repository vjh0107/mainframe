package kr.junhyung.mainframe.platform.velocity.discovery;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import kr.junhyung.mainframe.core.discovery.GameServerDiscovery;
import kr.junhyung.mainframe.core.discovery.GameServerServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameServerConnectionRouter {

    private static final Logger log = LoggerFactory.getLogger(GameServerConnectionRouter.class);

    private final ProxyServer proxyServer;
    private final GameServerDiscovery discovery;
    private final GameServerLoadBalancer loadBalancer;

    private final ConcurrentHashMap<UUID, Set<String>> attempts = new ConcurrentHashMap<>();

    public GameServerConnectionRouter(ProxyServer proxyServer, GameServerDiscovery discovery,
                                      GameServerLoadBalancer loadBalancer) {
        this.proxyServer = proxyServer;
        this.discovery = discovery;
        this.loadBalancer = loadBalancer;
    }

    public Optional<RegisteredServer> resolveInstance(String instanceId) {
        return proxyServer.getServer(instanceId);
    }

    public Optional<RegisteredServer> routeToService(String serviceName) {
        return routeToService(serviceName, Set.of());
    }

    public Optional<RegisteredServer> routeToService(String serviceName, Collection<String> exclude) {
        Set<String> instanceIds = discovery.getInstances(serviceName).stream()
                .map(GameServerServiceInstance::getInstanceId)
                .collect(Collectors.toUnmodifiableSet());
        return loadBalancer.choose(candidates(instanceIds, exclude));
    }

    public Optional<RegisteredServer> routeToEntrypoint() {
        return routeToEntrypoint(Set.of());
    }

    public Optional<RegisteredServer> routeToEntrypoint(Collection<String> exclude) {
        Set<String> instanceIds = discovery.getInstances().stream()
                .filter(GameServerServiceInstance::isEntrypoint)
                .map(GameServerServiceInstance::getInstanceId)
                .collect(Collectors.toUnmodifiableSet());
        return loadBalancer.choose(candidates(instanceIds, exclude));
    }

    @Subscribe
    public void onChooseInitialServer(PlayerChooseInitialServerEvent event) {
        routeToEntrypoint().ifPresent(event::setInitialServer);
    }

    @Subscribe
    public void onKickedFromServer(KickedFromServerEvent event) {
        Player player = event.getPlayer();
        if (event.kickedDuringServerConnect() && player.getCurrentServer().isPresent()) {
            return;
        }
        Set<String> attempted = recordAttempt(player.getUniqueId(), event.getServer().getServerInfo().getName());
        routeToEntrypoint(attempted).ifPresentOrElse(
                target -> event.setResult(KickedFromServerEvent.RedirectPlayer.create(target)),
                () -> log.warn("No fallback backend available to redirect {}", player.getUsername()));
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        attempts.remove(event.getPlayer().getUniqueId());
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        attempts.remove(event.getPlayer().getUniqueId());
    }

    private Set<String> recordAttempt(UUID playerId, String serverName) {
        Set<String> attempted = attempts.computeIfAbsent(playerId, ignored -> ConcurrentHashMap.newKeySet());
        attempted.add(serverName);
        return Set.copyOf(attempted);
    }

    private List<RegisteredServer> candidates(Set<String> instanceIds, Collection<String> exclude) {
        return proxyServer.getAllServers().stream()
                .filter(server -> instanceIds.contains(server.getServerInfo().getName()))
                .filter(server -> !exclude.contains(server.getServerInfo().getName()))
                .collect(Collectors.toList());
    }
}
