package kr.junhyung.mainframe.platform.velocity.presence;

import com.velocitypowered.api.proxy.ProxyServer;
import kr.junhyung.mainframe.core.presence.redis.RedisPlayerPresenceAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerPresenceReconcilerAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DataRedisAutoConfiguration.class,
                    RedisPlayerPresenceAutoConfiguration.class, PlayerPresenceReconcilerAutoConfiguration.class))
            .withBean(ProxyServer.class, () -> Mockito.mock(ProxyServer.class));

    @Test
    void offByDefault() {
        runner.run(context -> assertThat(context).doesNotHaveBean(PlayerPresenceReconciler.class));
    }

    @Test
    void onWhenPresenceEnabled() {
        runner.withPropertyValues("mainframe.presence.enabled=true", "mainframe.presence.instance-id=test")
                .run(context -> assertThat(context).hasSingleBean(PlayerPresenceReconciler.class));
    }

    @Test
    void backsOffWithoutProxyServer() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DataRedisAutoConfiguration.class,
                        RedisPlayerPresenceAutoConfiguration.class, PlayerPresenceReconcilerAutoConfiguration.class))
                .withPropertyValues("mainframe.presence.enabled=true", "mainframe.presence.instance-id=test")
                .run(context -> assertThat(context).doesNotHaveBean(PlayerPresenceReconciler.class));
    }
}
