package kr.junhyung.mainframe.platform.paper.modelengine.disguise;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import jakarta.annotation.PreDestroy;
import net.minecraft.network.HandlerNames;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

class DisguiseVisibilityInterceptor implements Listener {

    private static final String HANDLER_NAME = "mainframe_disguise_visibility";
    private static final int SHARED_FLAGS = 0;
    private static final int INVISIBLE_BIT = 5;

    private final ModelEngineDisguiseService disguises;

    DisguiseVisibilityInterceptor(ModelEngineDisguiseService disguises) {
        this.disguises = disguises;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Channel channel = channel(event.getPlayer());
        if (channel.pipeline().get(HANDLER_NAME) == null) {
            channel.pipeline().addBefore(HandlerNames.PACKET_HANDLER, HANDLER_NAME, new Handler());
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

    private Packet<? super ClientGamePacketListener> invisibility(int entityId) {
        Entity target = disguises.disguised(entityId);
        if (target == null) {
            return null;
        }
        byte flags = (byte) (sharedFlags(target) | 1 << INVISIBLE_BIT);
        return new ClientboundSetEntityDataPacket(entityId,
                List.of(new SynchedEntityData.DataValue<>(SHARED_FLAGS, EntityDataSerializers.BYTE, flags)));
    }

    private byte sharedFlags(Entity target) {
        List<SynchedEntityData.DataValue<?>> values =
                ((CraftEntity) target).getHandle().getEntityData().getNonDefaultValues();
        if (values == null) {
            return 0;
        }
        for (SynchedEntityData.DataValue<?> value : values) {
            if (value.id() == SHARED_FLAGS && value.value() instanceof Byte flags) {
                return flags;
            }
        }
        return 0;
    }

    private final class Handler extends ChannelOutboundHandlerAdapter {

        @Override
        public void write(ChannelHandlerContext context, Object message, ChannelPromise promise) throws Exception {
            if (message instanceof ClientboundBundlePacket bundle) {
                List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>();
                for (Packet<? super ClientGamePacketListener> packet : bundle.subPackets()) {
                    packets.add(packet);
                    if (packet instanceof ClientboundAddEntityPacket spawn) {
                        Packet<? super ClientGamePacketListener> extra = invisibility(spawn.getId());
                        if (extra != null) {
                            packets.add(extra);
                        }
                    }
                }
                super.write(context, new ClientboundBundlePacket(packets), promise);
                return;
            }
            if (message instanceof ClientboundAddEntityPacket spawn) {
                Packet<? super ClientGamePacketListener> extra = invisibility(spawn.getId());
                if (extra != null) {
                    super.write(context, message, context.voidPromise());
                    super.write(context, extra, promise);
                    return;
                }
            }
            super.write(context, message, promise);
        }
    }
}
