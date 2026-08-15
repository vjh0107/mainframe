package kr.junhyung.mainframe.platform.paper.nametag;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import io.papermc.paper.event.player.PlayerUntrackEntityEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerHideEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShowEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class NametagHandler implements Listener {

    private static final long GLIDE_RECOVERY_DELAY = 5L;
    private static final long SWEEP_INTERVAL = 1L;
    private static final long ZERO_DAMAGE_COOLDOWN = 100L;

    private final NametagServiceImpl service;
    private final Plugin plugin;
    private final Set<UUID> zeroDamageRecovering = ConcurrentHashMap.newKeySet();

    NametagHandler(NametagServiceImpl service, Plugin plugin) {
        this.service = service;
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimer(plugin, service::sweepRevived, SWEEP_INTERVAL, SWEEP_INTERVAL);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            service.died(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onZeroDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || event.getFinalDamage() > 0) {
            return;
        }
        UUID id = player.getUniqueId();
        if (!zeroDamageRecovering.add(id)) {
            return;
        }
        service.reattach(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> zeroDamageRecovering.remove(id), ZERO_DAMAGE_COOLDOWN);
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() == GameMode.SPECTATOR) {
            service.block(event.getPlayer());
        } else if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            service.unblock(event.getPlayer());
        }
    }

    @EventHandler
    public void onPotion(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player) || !isInvisibility(event)) {
            return;
        }
        if (event.getAction() == EntityPotionEffectEvent.Action.ADDED
                || event.getAction() == EntityPotionEffectEvent.Action.CHANGED) {
            service.block(player);
        } else {
            service.unblock(player);
        }
    }

    private boolean isInvisibility(EntityPotionEffectEvent event) {
        PotionEffectType type = event.getNewEffect() == null
                ? (event.getOldEffect() == null ? null : event.getOldEffect().getType())
                : event.getNewEffect().getType();
        return PotionEffectType.INVISIBILITY.equals(type);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        service.fade(event.getPlayer(), event.isSneaking());
    }

    @EventHandler
    public void onGlide(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (event.isGliding()) {
            service.detach(player, player);
        } else {
            service.reattachSelf(player, GLIDE_RECOVERY_DELAY);
        }
    }

    @EventHandler
    public void onStartSpectating(PlayerStartSpectatingEntityEvent event) {
        service.detach(event.getPlayer(), event.getPlayer());
    }

    @EventHandler
    public void onStopSpectating(PlayerStopSpectatingEntityEvent event) {
        service.reattachSelf(event.getPlayer(), 1L);
    }

    @EventHandler
    public void onTrack(PlayerTrackEntityEvent event) {
        Entity subject = event.getEntity();
        if (subject != event.getPlayer() && service.accepts(subject)) {
            service.attach(subject, event.getPlayer());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (service.accepts(player) && service.selfVisible(player)) {
            service.attach(player, player);
        }
    }

    @EventHandler
    public void onUntrack(PlayerUntrackEntityEvent event) {
        service.detach(event.getEntity(), event.getPlayer());
    }

    @EventHandler
    public void onHideEntity(PlayerHideEntityEvent event) {
        service.detach(event.getEntity(), event.getPlayer());
    }

    @EventHandler
    public void onShowEntity(PlayerShowEntityEvent event) {
        Entity subject = event.getEntity();
        if (service.accepts(subject)) {
            service.attach(subject, event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {
        service.respawned(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        service.reattach(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        service.resend(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        service.forget(event.getPlayer());
    }

    @EventHandler
    public void onRemove(EntityRemoveEvent event) {
        service.forget(event.getEntity());
    }
}
