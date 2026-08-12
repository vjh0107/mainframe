package kr.junhyung.mainframe.platform.velocity.presence;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.GameProfile;
import kr.junhyung.mainframe.core.presence.PlayerIdentity;
import kr.junhyung.mainframe.core.presence.PlayerPresenceCommandService;
import kr.junhyung.mainframe.core.presence.PlayerSkin;
import kr.junhyung.mainframe.core.scheduling.AbstractScheduledLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerPresenceReconciler extends AbstractScheduledLifecycle {

    private static final Logger log = LoggerFactory.getLogger(PlayerPresenceReconciler.class);
    private static final Duration SWEEP_INTERVAL = Duration.ofMinutes(5);

    private final ProxyServer proxyServer;
    private final PlayerPresenceCommandService presence;
    private final int sweepCycles;

    private int cyclesUntilSweep;

    public PlayerPresenceReconciler(ProxyServer proxyServer, PlayerPresenceCommandService presence,
                                    Duration resyncInterval) {
        super("player-presence-resync", resyncInterval);
        this.proxyServer = proxyServer;
        this.presence = presence;
        this.sweepCycles = (int) Math.max(1, SWEEP_INTERVAL.toMillis() / Math.max(1, resyncInterval.toMillis()));
    }

    @Override
    protected Duration initialDelay() {
        return Duration.ZERO;
    }

    @Override
    protected void runOnce() {
        presence.refresh(proxyServer.getAllPlayers().stream()
                .collect(Collectors.toMap(Player::getUniqueId, PlayerPresenceReconciler::identityOf)));
        sweep();
    }

    @Override
    public void stop() {
        remove(proxyServer.getAllPlayers().stream().map(Player::getUniqueId).toList());
        super.stop();
    }

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        Player player = event.getPlayer();
        try {
            presence.refresh(Map.of(player.getUniqueId(), identityOf(player)));
        } catch (RuntimeException e) {
            log.error("Failed to record presence for {}", player.getUsername(), e);
        }
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        remove(List.of(event.getPlayer().getUniqueId()));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        remove(proxyServer.getAllPlayers().stream().map(Player::getUniqueId).toList());
    }

    private static PlayerIdentity identityOf(Player player) {
        return new PlayerIdentity(player.getUsername(), skinOf(player));
    }

    private static PlayerSkin skinOf(Player player) {
        for (GameProfile.Property property : player.getGameProfile().getProperties()) {
            if ("textures".equals(property.getName()) && property.getSignature() != null) {
                return new PlayerSkin(property.getValue(), property.getSignature());
            }
        }
        return null;
    }

    private void sweep() {
        if (--cyclesUntilSweep > 0) {
            return;
        }
        cyclesUntilSweep = sweepCycles;
        int swept = presence.sweepUnexpiring();
        if (swept > 0) {
            log.warn("Swept {} presence entries left without a TTL", swept);
        }
    }

    private void remove(Collection<UUID> playerIds) {
        try {
            presence.remove(playerIds);
        } catch (RuntimeException e) {
            log.error("Failed to remove player presence", e);
        }
    }
}
