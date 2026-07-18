package kr.junhyung.mainframe.platform.velocity.discovery;

import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Collection;
import java.util.Optional;

public interface GameServerLoadBalancer {

    Optional<RegisteredServer> choose(Collection<RegisteredServer> candidates);
}
