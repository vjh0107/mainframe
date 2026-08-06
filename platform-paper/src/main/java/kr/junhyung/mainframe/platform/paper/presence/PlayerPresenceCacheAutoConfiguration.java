package kr.junhyung.mainframe.platform.paper.presence;

import kr.junhyung.mainframe.core.presence.redis.RedisPlayerPresenceAutoConfiguration;
import kr.junhyung.mainframe.core.presence.PlayerPresenceProperties;
import kr.junhyung.mainframe.core.presence.PlayerPresenceService;
import org.bukkit.Server;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@AutoConfiguration(after = RedisPlayerPresenceAutoConfiguration.class)
@ConditionalOnClass(Server.class)
public class PlayerPresenceCacheAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnBean(PlayerPresenceService.class)
    @ConditionalOnMissingBean
    public PlayerPresenceCache playerPresenceCache(PlayerPresenceService presence,
                                                   PlayerPresenceProperties properties) {
        return new PlayerPresenceCache(presence, properties.getCacheInterval(), properties.getTtl());
    }
}
