package kr.junhyung.mainframe.core.presence;

import java.util.UUID;

public record PlayerPresence(UUID playerId, String username, String instanceId) {
}
