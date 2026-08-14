package kr.junhyung.mainframe.platform.paper.nametag;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import jakarta.annotation.PreDestroy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.HandlerNames;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class NametagPassengerInterceptor implements Listener {

    private static final String HANDLER_NAME = "mainframe_nametag_passengers";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final NametagServiceImpl service;

    NametagPassengerInterceptor(NametagServiceImpl service) {
        this.service = service;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Channel channel = channel(event.getPlayer());
        if (channel.pipeline().get(HANDLER_NAME) == null) {
            channel.pipeline().addBefore(HandlerNames.PACKET_HANDLER, HANDLER_NAME, new Handler(event.getPlayer()));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        eject(event.getPlayer());
    }

    @PreDestroy
    void shutdown() {
        Bukkit.getOnlinePlayers().forEach(this::eject);
    }

    private void eject(Player player) {
        Channel channel = channel(player);
        channel.eventLoop().execute(() -> {
            if (channel.pipeline().get(HANDLER_NAME) != null) {
                channel.pipeline().remove(HANDLER_NAME);
            }
        });
    }

    private Channel channel(Player player) {
        return ((CraftPlayer) player).getHandle().connection.connection.channel;
    }

    private Packet<?> rewrite(Packet<?> packet, Player viewer) {
        return switch (packet) {
            case ClientboundBundlePacket bundle -> rewriteBundle(bundle, viewer);
            case ClientboundSetPassengersPacket passengers -> rewritePassengers(passengers, viewer);
            case ClientboundSetCameraPacket camera -> observeCamera(camera, viewer);
            default -> packet;
        };
    }

    @SuppressWarnings("unchecked")
    private Packet<?> rewriteBundle(ClientboundBundlePacket bundle, Player viewer) {
        List<Packet<? super ClientGamePacketListener>> rewritten = new ArrayList<>();
        boolean modified = false;
        for (Packet<? super ClientGamePacketListener> sub : bundle.subPackets()) {
            Packet<?> next = rewrite(sub, viewer);
            modified |= next != sub;
            rewritten.add((Packet<? super ClientGamePacketListener>) next);
        }
        return modified ? new ClientboundBundlePacket(rewritten) : bundle;
    }

    private Packet<?> observeCamera(ClientboundSetCameraPacket packet, Player viewer) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        ClientboundSetCameraPacket.STREAM_CODEC.encode(buffer, packet);
        service.cameraChanged(viewer, buffer.readVarInt() == viewer.getEntityId());
        return packet;
    }

    private Packet<?> rewritePassengers(ClientboundSetPassengersPacket packet, Player viewer) {
        int[] merged = service.mergePassengers(packet.getVehicle(), viewer, packet.getPassengers());
        if (merged == null) {
            return packet;
        }
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeVarInt(packet.getVehicle());
        buffer.writeVarIntArray(merged);
        return ClientboundSetPassengersPacket.STREAM_CODEC.decode(buffer);
    }

    private final class Handler extends ChannelOutboundHandlerAdapter {

        private final Player viewer;

        private Handler(Player viewer) {
            this.viewer = viewer;
        }

        @Override
        public void write(ChannelHandlerContext context, Object message, ChannelPromise promise) throws Exception {
            if (message instanceof Packet<?> packet) {
                try {
                    message = rewrite(packet, viewer);
                } catch (RuntimeException e) {
                    log.error("Failed to merge nametag passengers into {}", packet.getClass().getSimpleName(), e);
                }
            }
            super.write(context, message, promise);
        }
    }
}
