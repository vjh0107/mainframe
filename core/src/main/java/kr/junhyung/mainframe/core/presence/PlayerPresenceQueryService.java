package kr.junhyung.mainframe.core.presence;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface PlayerPresenceQueryService {

    boolean isOnline(UUID playerId);

    long count();

    Map<UUID, PlayerPresence> online();

    Optional<PlayerPresence> find(UUID playerId);

    Optional<PlayerPresence> findByName(String username);
}
