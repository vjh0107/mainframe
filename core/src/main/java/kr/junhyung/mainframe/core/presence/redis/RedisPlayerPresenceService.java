package kr.junhyung.mainframe.core.presence.redis;

import kr.junhyung.mainframe.core.presence.PlayerIdentity;
import kr.junhyung.mainframe.core.presence.PlayerPresence;
import kr.junhyung.mainframe.core.presence.PlayerSkin;
import kr.junhyung.mainframe.core.presence.PlayerPresenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class RedisPlayerPresenceService implements PlayerPresenceService, InitializingBean {

    private static final String REFRESH_SCRIPT_LOCATION =
            "kr/junhyung/mainframe/core/presence/redis/refresh.lua";
    private static final String REMOVE_OWNED_SCRIPT_LOCATION =
            "kr/junhyung/mainframe/core/presence/redis/remove-owned.lua";
    private static final String SWEEP_SCRIPT_LOCATION =
            "kr/junhyung/mainframe/core/presence/redis/sweep.lua";

    static final char SEPARATOR = '/';
    static final char SKIN_SEPARATOR = ';';

    private static final Logger log = LoggerFactory.getLogger(RedisPlayerPresenceService.class);
    private static final int BATCH = 500;
    private static final RedisScript<Long> REFRESH = script(REFRESH_SCRIPT_LOCATION);
    private static final RedisScript<Long> REMOVE_OWNED = script(REMOVE_OWNED_SCRIPT_LOCATION);
    private static final RedisScript<Long> SWEEP = script(SWEEP_SCRIPT_LOCATION);

    private final StringRedisTemplate redis;
    private final String key;
    private final Duration ttl;
    private final String instanceId;
    private final List<String> keys;

    RedisPlayerPresenceService(StringRedisTemplate redis, String key, Duration ttl, String instanceId) {
        if (instanceId.indexOf(SEPARATOR) >= 0 || instanceId.indexOf(SKIN_SEPARATOR) >= 0) {
            throw new IllegalArgumentException("Player presence instance id must not contain '" + SEPARATOR
                    + "' or '" + SKIN_SEPARATOR + "': " + instanceId);
        }
        this.redis = redis;
        this.key = key;
        this.ttl = ttl;
        this.instanceId = instanceId;
        this.keys = List.of(key);
    }

    @Override
    public void afterPropertiesSet() {
        String probe = key + ":probe";
        try {
            redis.execute(REFRESH, List.of(probe), "1", "probe", instanceId);
        } catch (RedisConnectionFailureException e) {
            log.warn("Could not verify Redis supports HEXPIRE; Redis was unreachable at startup", e);
        } catch (DataAccessException e) {
            throw new IllegalStateException("Player presence requires Redis 7.4 or newer for HEXPIRE", e);
        } finally {
            discard(probe);
        }
    }

    @Override
    public void refresh(Map<UUID, PlayerIdentity> players) {
        if (players.isEmpty()) {
            return;
        }
        String ttlArg = Long.toString(ttl.toSeconds());
        List<String> args = new ArrayList<>();
        args.add(ttlArg);
        for (Map.Entry<UUID, PlayerIdentity> player : players.entrySet()) {
            args.add(player.getKey().toString());
            args.add(encode(player.getValue()));
            if (args.size() > BATCH * 2) {
                flush(REFRESH, args, ttlArg);
            }
        }
        flush(REFRESH, args, ttlArg);
    }

    @Override
    public void remove(Collection<UUID> playerIds) {
        if (playerIds.isEmpty()) {
            return;
        }
        String owner = instanceId + SEPARATOR;
        List<String> args = new ArrayList<>();
        args.add(owner);
        for (UUID playerId : playerIds) {
            args.add(playerId.toString());
            if (args.size() > BATCH) {
                flush(REMOVE_OWNED, args, owner);
            }
        }
        flush(REMOVE_OWNED, args, owner);
    }

    @Override
    public int sweepUnexpiring() {
        Long removed = redis.execute(SWEEP, keys);
        return removed == null ? 0 : removed.intValue();
    }

    @Override
    public boolean isOnline(UUID playerId) {
        return Boolean.TRUE.equals(redis.opsForHash().hasKey(key, playerId.toString()));
    }

    @Override
    public long count() {
        return redis.opsForHash().size(key);
    }

    @Override
    public Map<UUID, PlayerPresence> online() {
        Map<String, String> entries = redis.<String, String>opsForHash().entries(key);
        Map<UUID, PlayerPresence> players = new HashMap<>(entries.size());
        entries.forEach((playerId, value) -> {
            UUID id = UUID.fromString(playerId);
            players.put(id, parse(id, value));
        });
        return Map.copyOf(players);
    }

    @Override
    public Optional<PlayerPresence> find(UUID playerId) {
        Object value = redis.opsForHash().get(key, playerId.toString());
        return value == null ? Optional.empty() : Optional.of(parse(playerId, value.toString()));
    }

    @Override
    public Optional<PlayerPresence> findByName(String username) {
        return online().values().stream()
                .filter(player -> player.username().equalsIgnoreCase(username))
                .findFirst();
    }

    private void flush(RedisScript<Long> script, List<String> args, String header) {
        if (args.size() < 2) {
            return;
        }
        redis.execute(script, keys, args.toArray());
        args.clear();
        args.add(header);
    }

    private void discard(String probe) {
        try {
            redis.delete(probe);
        } catch (DataAccessException e) {
            log.debug("Failed to remove the presence probe key {}", probe, e);
        }
    }

    private static RedisScript<Long> script(String location) {
        return RedisScript.of(
                new ClassPathResource(location, RedisPlayerPresenceService.class.getClassLoader()), Long.class);
    }

    private String encode(PlayerIdentity identity) {
        String prefix = instanceId + SEPARATOR + identity.username();
        PlayerSkin skin = identity.skin();
        return skin == null ? prefix : prefix + SKIN_SEPARATOR + skin.value() + SKIN_SEPARATOR + skin.signature();
    }

    static PlayerPresence parse(UUID playerId, String value) {
        int separator = value.indexOf(SEPARATOR);
        String owner = separator < 0 ? null : value.substring(0, separator);
        String remainder = separator < 0 ? value : value.substring(separator + 1);
        int skinStart = remainder.indexOf(SKIN_SEPARATOR);
        if (skinStart < 0) {
            return new PlayerPresence(playerId, remainder, owner, null);
        }
        int signatureStart = remainder.indexOf(SKIN_SEPARATOR, skinStart + 1);
        PlayerSkin skin = new PlayerSkin(remainder.substring(skinStart + 1, signatureStart),
                remainder.substring(signatureStart + 1));
        return new PlayerPresence(playerId, remainder.substring(0, skinStart), owner, skin);
    }
}
