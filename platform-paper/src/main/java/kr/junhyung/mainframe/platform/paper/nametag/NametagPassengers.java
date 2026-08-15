package kr.junhyung.mainframe.platform.paper.nametag;

import org.bukkit.entity.Entity;

import java.util.List;

public interface NametagPassengers {

    List<Integer> of(Entity subject);

    void remount(Entity subject);
}
