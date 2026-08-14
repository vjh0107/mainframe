package kr.junhyung.mainframe.platform.paper.nametag;

import io.netty.buffer.Unpooled;
import kr.junhyung.mainframe.platform.paper.hologram.Hologram;
import net.kyori.adventure.text.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class NametagInstance {

    private final Entity subject;
    private final Map<String, Hologram> lines = new ConcurrentHashMap<>();
    private final Map<String, Set<UUID>> spawned = new ConcurrentHashMap<>();

    NametagInstance(Entity subject) {
        this.subject = subject;
    }

    Entity subject() {
        return subject;
    }

    Hologram line(Nametag nametag) {
        return lines.computeIfAbsent(nametag.id(), id -> {
            Hologram hologram = new Hologram(subject.getLocation(), nametag.scale());
            hologram.edit(nametag::style);
            hologram.translate(nametag.offset());
            return hologram;
        });
    }

    Hologram existing(String id) {
        return lines.get(id);
    }

    Packet<?> attach(Collection<Nametag> shown, Player viewer) {
        List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>();
        Location at = mountPoint();
        UUID viewerId = viewer.getUniqueId();
        for (Nametag nametag : shown) {
            Hologram hologram = line(nametag);
            Component text = nametag.text(subject, viewer);
            if (viewers(nametag.id()).add(viewerId)) {
                hologram.spawnPackets(text, at, 0f, packets);
            } else {
                hologram.dataPackets(text, packets);
            }
        }
        packets.add(mount(shown));
        return new ClientboundBundlePacket(packets);
    }

    private Set<UUID> viewers(String id) {
        return spawned.computeIfAbsent(id, key -> ConcurrentHashMap.newKeySet());
    }

    void forgetSpawned() {
        spawned.clear();
    }

    private Location mountPoint() {
        return subject.getLocation().add(0, subject.getHeight(), 0);
    }

    void hide(Player viewer) {
        UUID viewerId = viewer.getUniqueId();
        lines.forEach((id, hologram) -> {
            viewers(id).remove(viewerId);
            hologram.hide(viewer);
        });
    }

    private ClientboundSetPassengersPacket mount(Collection<Nametag> shown) {
        List<Integer> passengers = new ArrayList<>();
        subject.getPassengers().forEach(passenger -> passengers.add(entityId(passenger)));
        shown.forEach(nametag -> passengers.add(line(nametag).entityId()));
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeVarInt(entityId(subject));
        buffer.writeVarIntArray(passengers.stream().mapToInt(Integer::intValue).toArray());
        return ClientboundSetPassengersPacket.STREAM_CODEC.decode(buffer);
    }

    private static int entityId(Entity entity) {
        return ((CraftEntity) entity).getHandle().getId();
    }

    static void send(Player viewer, Packet<?> packet) {
        if (viewer.isOnline()) {
            ((CraftPlayer) viewer).getHandle().connection.send(packet);
        }
    }
}
