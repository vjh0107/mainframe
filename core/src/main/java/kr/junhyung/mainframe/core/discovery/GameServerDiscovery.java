package kr.junhyung.mainframe.core.discovery;

import java.util.List;

public interface GameServerDiscovery {

    List<GameServerServiceInstance> getInstances();

    List<GameServerServiceInstance> getInstances(String serviceId);
}
