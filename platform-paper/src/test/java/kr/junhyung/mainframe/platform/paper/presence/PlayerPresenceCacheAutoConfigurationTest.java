package kr.junhyung.mainframe.platform.paper.presence;

import kr.junhyung.mainframe.core.presence.redis.RedisPlayerPresenceAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerPresenceCacheAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DataRedisAutoConfiguration.class,
                    RedisPlayerPresenceAutoConfiguration.class, PlayerPresenceCacheAutoConfiguration.class));

    @Test
    void offByDefault() {
        runner.run(context -> assertThat(context).doesNotHaveBean(PlayerPresenceCache.class));
    }

    @Test
    void onWhenPresenceEnabled() {
        runner.withPropertyValues("mainframe.presence.enabled=true", "mainframe.presence.instance-id=test")
                .run(context -> assertThat(context).hasSingleBean(PlayerPresenceCache.class));
    }

    @Test
    void failsWithoutAnInstanceId() {
        runner.withPropertyValues("mainframe.presence.enabled=true", "HOSTNAME=")
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void fallsBackToHostname() {
        runner.withPropertyValues("mainframe.presence.enabled=true", "HOSTNAME=proxy-0")
                .run(context -> assertThat(context).hasSingleBean(PlayerPresenceCache.class));
    }

    @Test
    void failsWhenTtlCannotSurviveMissedRefreshes() {
        runner.withPropertyValues("mainframe.presence.enabled=true", "mainframe.presence.instance-id=test",
                        "mainframe.presence.ttl=20s",
                        "mainframe.presence.resync-interval=15s")
                .run(context -> assertThat(context).hasFailed());
    }
}
