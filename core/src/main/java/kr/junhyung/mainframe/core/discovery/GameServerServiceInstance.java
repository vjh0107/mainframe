package kr.junhyung.mainframe.core.discovery;

import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.util.Map;

public class GameServerServiceInstance implements ServiceInstance {

    private final ServiceInstance delegate;

    public GameServerServiceInstance(ServiceInstance delegate) {
        this.delegate = delegate;
    }

    public boolean isEntrypoint() {
        Map<String, String> metadata = getMetadata();
        if (metadata == null) {
            return false;
        }
        return Boolean.parseBoolean(metadata.get(GameServerMetadata.ENTRYPOINT));
    }

    public int getMaxPlayers() {
        Map<String, String> metadata = getMetadata();
        if (metadata == null) {
            return 0;
        }
        return Integer.parseInt(metadata.get(GameServerMetadata.MAX_PLAYERS));
    }

    @Override
    public String getInstanceId() {
        return delegate.getInstanceId();
    }

    @Override
    public String getServiceId() {
        return delegate.getServiceId();
    }

    @Override
    public String getHost() {
        return delegate.getHost();
    }

    @Override
    public int getPort() {
        return delegate.getPort();
    }

    @Override
    public boolean isSecure() {
        return delegate.isSecure();
    }

    @Override
    public URI getUri() {
        return delegate.getUri();
    }

    @Override
    public Map<String, String> getMetadata() {
        return delegate.getMetadata();
    }

    @Override
    public String getScheme() {
        return delegate.getScheme();
    }

}
