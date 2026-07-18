package kr.junhyung.mainframe.platform.velocity.discovery;

import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class P2CGameServerLoadBalancer implements GameServerLoadBalancer {

    @Override
    public Optional<RegisteredServer> choose(Collection<RegisteredServer> candidates) {
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        List<RegisteredServer> servers = new ArrayList<>(candidates);
        if (servers.size() == 1) {
            return Optional.of(servers.getFirst());
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int first = random.nextInt(servers.size());
        int second = random.nextInt(servers.size() - 1);
        if (second >= first) {
            second++;
        }
        RegisteredServer candidate = servers.get(first);
        RegisteredServer other = servers.get(second);
        return Optional.of(load(candidate) <= load(other) ? candidate : other);
    }

    private static int load(RegisteredServer server) {
        return server.getPlayersConnected().size();
    }

}
