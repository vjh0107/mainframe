package kr.junhyung.mainframe.platform.paper.hologram;

import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public final class Hologram {

    private final Location location;
    private final Vector3f scale;
    private final int entityId = Entity.nextEntityId();
    private final UUID uuid = UUID.randomUUID();
    private final Display.TextDisplay handle;

    public Hologram(Location location, float scale) {
        this(location, new Vector3f(scale, scale, scale));
    }

    public Hologram(Location location, Vector3f scale) {
        this.location = location;
        this.scale = new Vector3f(scale);
        this.handle = new Display.TextDisplay(EntityType.TEXT_DISPLAY, ((CraftWorld) location.getWorld()).getHandle());
        this.handle.setId(entityId);
        this.handle.setUUID(uuid);
        translate(0f, 0f, 0f);
    }

    public int entityId() {
        return entityId;
    }

    public void edit(Consumer<TextDisplay> editor) {
        editor.accept((TextDisplay) handle.getBukkitEntity());
    }

    public void translate(Vector3f offset) {
        translate(offset.x(), offset.y(), offset.z());
    }

    public void translate(float offsetX, float offsetY) {
        translate(offsetX, offsetY, 0f);
    }

    public void translate(float offsetX, float offsetY, float offsetZ) {
        edit(display -> {
            display.setInterpolationDuration(0);
            display.setInterpolationDelay(0);
            display.setTransformation(new Transformation(new Vector3f(offsetX, offsetY, offsetZ), new Quaternionf(),
                    new Vector3f(scale), new Quaternionf()));
        });
    }

    public Location location() {
        return location;
    }

    public void show(Player viewer, Component text) {
        show(viewer, text, location, 0f);
    }

    public void show(Player viewer, Component text, Location at, float yaw) {
        apply(text);
        send(viewer, new ClientboundAddEntityPacket(entityId, uuid,
                at.getX(), at.getY(), at.getZ(), 0f, yaw,
                EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, yaw));
        sendData(viewer);
    }

    public void place(Player viewer, Location at, float yaw) {
        send(viewer, new ClientboundTeleportEntityPacket(entityId,
                new PositionMoveRotation(new Vec3(at.getX(), at.getY(), at.getZ()), Vec3.ZERO, yaw, 0f),
                Set.of(), false));
    }

    public void update(Player viewer, Component text) {
        apply(text);
        sendData(viewer);
    }

    public void refresh(Player viewer) {
        sendData(viewer);
    }

    public void hide(Player viewer) {
        send(viewer, new ClientboundRemoveEntitiesPacket(entityId));
    }

    public void moveTo(Player viewer, Location target) {
        location.set(target.getX(), target.getY(), target.getZ());
        send(viewer, new ClientboundTeleportEntityPacket(entityId,
                new PositionMoveRotation(new Vec3(target.getX(), target.getY(), target.getZ()), Vec3.ZERO, 0f, 0f),
                Set.of(), false));
    }

    public void spawnPackets(Component text, Location at, float yaw,
                             List<Packet<? super ClientGamePacketListener>> out) {
        apply(text);
        out.add(new ClientboundAddEntityPacket(entityId, uuid, at.getX(), at.getY(), at.getZ(), 0f, yaw,
                EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, yaw));
        out.add(new ClientboundSetEntityDataPacket(entityId, handle.getEntityData().packAll()));
    }

    public void dataPackets(Component text, List<Packet<? super ClientGamePacketListener>> out) {
        apply(text);
        out.add(new ClientboundSetEntityDataPacket(entityId, handle.getEntityData().packAll()));
    }

    private void apply(Component text) {
        ((TextDisplay) handle.getBukkitEntity()).text(text);
    }

    private void sendData(Player viewer) {
        send(viewer, new ClientboundSetEntityDataPacket(entityId, handle.getEntityData().packAll()));
    }

    private void send(Player viewer, Packet<?> packet) {
        ((CraftPlayer) viewer).getHandle().connection.send(packet);
    }
}
