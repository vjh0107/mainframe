package kr.junhyung.mainframe.core.presence.redis;

import com.redis.testcontainers.RedisContainer;
import kr.junhyung.mainframe.core.presence.PlayerIdentity;
import kr.junhyung.mainframe.core.presence.PlayerPresence;
import kr.junhyung.mainframe.core.presence.PlayerSkin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Testcontainers
class RedisPlayerPresenceServiceTest {

    private static final String KEY = "test:presence";
    private static final Duration TTL = Duration.ofSeconds(2);

    @Container
    static final RedisContainer REDIS = new RedisContainer("redis:8-alpine");

    private static StringRedisTemplate redis;

    private RedisPlayerPresenceService alpha;
    private RedisPlayerPresenceService beta;

    @BeforeEach
    void setUp() {
        if (redis == null) {
            LettuceConnectionFactory factory =
                    new LettuceConnectionFactory(REDIS.getHost(), REDIS.getFirstMappedPort());
            factory.afterPropertiesSet();
            redis = new StringRedisTemplate(factory);
            redis.afterPropertiesSet();
        }
        redis.delete(KEY);
        alpha = new RedisPlayerPresenceService(redis, KEY, TTL, "proxy-alpha");
        beta = new RedisPlayerPresenceService(redis, KEY, TTL, "proxy-beta");
    }

    @Test
    void refreshedPlayersAreOnlineWithTheirProxy() {
        UUID player = UUID.randomUUID();
        alpha.refresh(Map.of(player, identity("alice")));

        assertThat(alpha.isOnline(player)).isTrue();
        assertThat(alpha.count()).isEqualTo(1);
        assertThat(alpha.findByName("ALICE")).map(PlayerPresence::instanceId).contains("proxy-alpha");
    }

    @Test
    void skinsSurviveTheRoundTripDespiteBase64Separators() {
        UUID player = UUID.randomUUID();
        PlayerSkin skin = new PlayerSkin("ey/JICAidGltZXN0YW1w+In0=", "D24y/zbg+aBETxe5e/acQ==");
        alpha.refresh(Map.of(player, new PlayerIdentity("alice", skin)));

        assertThat(alpha.find(player)).hasValueSatisfying(presence -> {
            assertThat(presence.username()).isEqualTo("alice");
            assertThat(presence.instanceId()).isEqualTo("proxy-alpha");
            assertThat(presence.skin()).isEqualTo(skin);
        });
    }

    @Test
    void everyRefreshLeavesATtlOnEveryField() {
        Map<UUID, PlayerIdentity> players = Map.of(UUID.randomUUID(), identity("alice"),
                UUID.randomUUID(), identity("bob"));
        alpha.refresh(players);
        alpha.refresh(players);

        assertThat(ttls(players.keySet().stream().map(UUID::toString).toList()))
                .allSatisfy(ttl -> assertThat(ttl).isPositive());
    }

    @Test
    void expiresPerFieldSoOneProxyDyingLeavesTheOthers() {
        UUID crashed = UUID.randomUUID();
        UUID alive = UUID.randomUUID();
        alpha.refresh(Map.of(crashed, identity("crashed")));
        beta.refresh(Map.of(alive, identity("alive")));

        await().pollInterval(Duration.ofMillis(200)).atMost(TTL.plusSeconds(3)).untilAsserted(() -> {
            beta.refresh(Map.of(alive, identity("alive")));
            assertThat(beta.isOnline(crashed)).isFalse();
        });
        assertThat(beta.isOnline(alive)).isTrue();
    }

    @Test
    void removeOnlyTouchesOwnPlayers() {
        UUID player = UUID.randomUUID();
        beta.refresh(Map.of(player, identity("alice")));

        alpha.remove(List.of(player));

        assertThat(beta.isOnline(player)).isTrue();
    }

    @Test
    void removeDropsOwnPlayersImmediately() {
        UUID player = UUID.randomUUID();
        alpha.refresh(Map.of(player, identity("alice")));

        alpha.remove(List.of(player));

        assertThat(alpha.isOnline(player)).isFalse();
    }

    @Test
    void sweepRemovesFieldsLeftWithoutATtl() {
        UUID ghost = UUID.randomUUID();
        UUID live = UUID.randomUUID();
        redis.opsForHash().put(KEY, ghost.toString(), "proxy-dead/ghost");
        alpha.refresh(Map.of(live, identity("alice")));

        assertThat(alpha.sweepUnexpiring()).isEqualTo(1);
        assertThat(alpha.isOnline(ghost)).isFalse();
        assertThat(alpha.isOnline(live)).isTrue();
    }

    @Test
    void refreshBeyondOneBatchStillExpires() {
        Map<UUID, PlayerIdentity> players = new java.util.HashMap<>();
        for (int i = 0; i < 1200; i++) {
            players.put(UUID.randomUUID(), identity("player" + i));
        }
        alpha.refresh(players);

        assertThat(alpha.count()).isEqualTo(1200);
        assertThat(alpha.sweepUnexpiring()).isZero();
    }

    private PlayerIdentity identity(String username) {
        return new PlayerIdentity(username, null);
    }

    private List<Long> ttls(List<String> fields) {
        return redis.execute((org.springframework.data.redis.core.RedisCallback<List<Long>>) connection ->
                connection.hashCommands().hTtl(KEY.getBytes(),
                        fields.stream().map(String::getBytes).toArray(byte[][]::new)));
    }
}
