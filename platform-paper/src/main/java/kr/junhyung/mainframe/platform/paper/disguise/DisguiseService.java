package kr.junhyung.mainframe.platform.paper.disguise;

import org.bukkit.entity.Entity;

public interface DisguiseService {

    void apply(Entity subject, String model);

    void clear(Entity subject, String model);

    boolean isDisguised(Entity subject);

    boolean isDisguised(Entity subject, String model);
}
