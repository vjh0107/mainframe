package kr.junhyung.mainframe.core.presence.redis;

import kr.junhyung.mainframe.core.presence.PlayerPresenceProperties;
import kr.junhyung.mainframe.core.presence.PlayerPresenceService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

@AutoConfiguration(afterName = "org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration")
@ConditionalOnClass(StringRedisTemplate.class)
@ConditionalOnProperty(prefix = PlayerPresenceProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(PlayerPresenceProperties.class)
public class RedisPlayerPresenceAutoConfiguration {

    @Bean
    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnMissingBean
    public PlayerPresenceService playerPresenceService(StringRedisTemplate redis,
                                                       PlayerPresenceProperties properties,
                                                       Environment environment) {
        if (properties.getTtl().compareTo(properties.getResyncInterval().multipliedBy(2)) <= 0) {
            throw new IllegalStateException(PlayerPresenceProperties.PREFIX + ".ttl (" + properties.getTtl()
                    + ") must be more than twice the refresh-interval (" + properties.getResyncInterval()
                    + "), otherwise presence flaps whenever a refresh is missed.");
        }
        return new RedisPlayerPresenceService(redis, properties.getKey(), properties.getTtl(),
                instanceId(properties, environment));
    }

    private String instanceId(PlayerPresenceProperties properties, Environment environment) {
        if (StringUtils.hasText(properties.getInstanceId())) {
            return properties.getInstanceId();
        }
        String hostname = environment.getProperty("HOSTNAME");
        if (StringUtils.hasText(hostname)) {
            return hostname;
        }
        throw new IllegalStateException(PlayerPresenceProperties.PREFIX
                + ".instance-id must be set; it identifies which instance owns a presence entry and HOSTNAME"
                + " is not available to fall back on");
    }
}
