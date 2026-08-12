package kr.junhyung.mainframe.core.presence;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface PlayerPresenceCommandService {

    void refresh(Map<UUID, PlayerIdentity> players);

    void remove(Collection<UUID> playerIds);

    int sweepUnexpiring();
}
