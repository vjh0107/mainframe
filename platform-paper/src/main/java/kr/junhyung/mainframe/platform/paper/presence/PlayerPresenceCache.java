package kr.junhyung.mainframe.platform.paper.presence;

import kr.junhyung.mainframe.core.presence.PlayerPresence;
import kr.junhyung.mainframe.core.presence.PlayerPresenceQueryService;
import kr.junhyung.mainframe.core.scheduling.AbstractScheduledLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class PlayerPresenceCache extends AbstractScheduledLifecycle implements PlayerPresenceQueryService {

    private static final Logger log = LoggerFactory.getLogger(PlayerPresenceCache.class);

    private final PlayerPresenceQueryService delegate;
    private final Duration staleAfter;

    private volatile Map<UUID, PlayerPresence> snapshot = Map.of();
    private volatile Instant refreshedAt;
    private volatile long refreshedNanos;
    private volatile boolean failing;

    public PlayerPresenceCache(PlayerPresenceQueryService delegate, Duration interval, Duration staleAfter) {
        super("player-presence-cache", interval);
        this.delegate = delegate;
        this.staleAfter = staleAfter;
    }

    @Override
    protected Duration initialDelay() {
        return Duration.ZERO;
    }

    @Override
    public Map<UUID, PlayerPresence> online() {
        return snapshot;
    }

    @Override
    public long count() {
        return snapshot.size();
    }

    @Override
    public boolean isOnline(UUID playerId) {
        return snapshot.containsKey(playerId);
    }

    @Override
    public Optional<PlayerPresence> find(UUID playerId) {
        return Optional.ofNullable(snapshot.get(playerId));
    }

    @Override
    public Optional<PlayerPresence> findByName(String username) {
        return snapshot.values().stream()
                .filter(player -> player.username().equalsIgnoreCase(username))
                .findFirst();
    }

    public Optional<String> nameOf(UUID playerId) {
        return find(playerId).map(PlayerPresence::username);
    }

    public Stream<String> namesStartingWith(String prefix) {
        String lower = prefix.toLowerCase(Locale.ROOT);
        return snapshot.values().stream()
                .map(PlayerPresence::username)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(lower));
    }

    public Optional<Instant> lastRefresh() {
        return Optional.ofNullable(refreshedAt);
    }

    public boolean isStale() {
        return refreshedAt == null || System.nanoTime() - refreshedNanos > staleAfter.toNanos();
    }

    @Override
    protected void runOnce() {
        try {
            snapshot = delegate.online();
            refreshedNanos = System.nanoTime();
            refreshedAt = Instant.now();
            if (failing) {
                failing = false;
                log.info("Player presence cache recovered");
            }
        } catch (RuntimeException e) {
            if (!failing) {
                failing = true;
                log.warn("Player presence cache is failing; serving the last snapshot", e);
            }
        }
    }
}
