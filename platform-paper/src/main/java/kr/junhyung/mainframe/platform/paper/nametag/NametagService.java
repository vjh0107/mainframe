package kr.junhyung.mainframe.platform.paper.nametag;

import org.bukkit.entity.Entity;

public interface NametagService {

    void refresh(Entity subject);

    void refresh(Entity subject, String id);

    void hide(Entity subject);

    void show(Entity subject);
}
