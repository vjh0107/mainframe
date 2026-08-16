package kr.junhyung.mainframe.platform.paper.disguise;

import org.bukkit.entity.Entity;

import java.util.List;

public interface DisguisePassengers {

    List<Integer> of(Entity subject);

    void remount(Entity subject);
}
