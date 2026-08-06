package kr.junhyung.mainframe.core.presence;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(PlayerPresenceProperties.PREFIX)
public class PlayerPresenceProperties {

    public static final String PREFIX = "mainframe.presence";

    private boolean enabled = false;

    private String key = "mainframe:presence";

    private String instanceId;

    private Duration ttl = Duration.ofSeconds(45);

    private Duration resyncInterval = Duration.ofSeconds(15);

    private Duration cacheInterval = Duration.ofSeconds(3);

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Duration getTtl() {
        return ttl;
    }

    public void setTtl(Duration ttl) {
        this.ttl = ttl;
    }

    public Duration getResyncInterval() {
        return resyncInterval;
    }

    public void setResyncInterval(Duration resyncInterval) {
        this.resyncInterval = resyncInterval;
    }

    public Duration getCacheInterval() {
        return cacheInterval;
    }

    public void setCacheInterval(Duration cacheInterval) {
        this.cacheInterval = cacheInterval;
    }
}
