package kr.junhyung.mainframe.platform.paper.nametag;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.joml.Vector3f;

public interface Nametag {

    String id();

    Vector3f offset();

    Component text(Entity subject, Player viewer);

    default boolean appliesTo(Entity subject) {
        return true;
    }

    default boolean selfVisible() {
        return false;
    }

    default Vector3f scale() {
        return new Vector3f(1f, 1f, 1f);
    }

    default void style(TextDisplay display) {
    }
}
