package kr.junhyung.mainframe.core.discovery;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(GameServerDiscoveryProperties.PREFIX)
public class GameServerDiscoveryProperties {

    public static final String PREFIX = "mainframe.discovery";

    private String serviceName;

    private String instanceId;

    private String displayName;

    private boolean entrypoint = false;

    private Duration resyncInterval = Duration.ofSeconds(15);

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isEntrypoint() {
        return entrypoint;
    }

    public void setEntrypoint(boolean entrypoint) {
        this.entrypoint = entrypoint;
    }

    public Duration getResyncInterval() {
        return resyncInterval;
    }

    public void setResyncInterval(Duration resyncInterval) {
        this.resyncInterval = resyncInterval;
    }
}
