package kr.junhyung.mainframe.platform.paper.nametag;

import kr.junhyung.mainframe.platform.paper.hologram.Hologram;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class NametagServiceImpl implements NametagService {

    private static final long ATTACH_DELAY = 1L;
    private static final long CAMERA_RECOVERY_DELAY = 1L;
    private static final long[] RECOVERY_DELAYS = {5L, 20L, 60L, 100L};
    private static final byte FADED_TEXT_OPACITY = 70;
    private static final Color FADED_BACKGROUND = Color.fromARGB(24, 0, 0, 0);

    private final Map<String, Nametag> nametags = new LinkedHashMap<>();
    private final Map<UUID, NametagInstance> instances = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> recoveries = new ConcurrentHashMap<>();
    private final Set<UUID> blocked = ConcurrentHashMap.newKeySet();
    private final Set<UUID> dead = ConcurrentHashMap.newKeySet();
    private final Plugin plugin;

    NametagServiceImpl(List<Nametag> nametags, Plugin plugin) {
        this.plugin = plugin;
        for (Nametag nametag : nametags) {
            Nametag previous = this.nametags.putIfAbsent(nametag.id(), nametag);
            if (previous != null) {
                throw new IllegalStateException("Duplicate nametag id: " + nametag.id());
            }
        }
    }

    @Override
    public void refresh(Entity subject) {
        onMainThread(() -> nametags.values().forEach(nametag -> refresh(subject, nametag)));
    }

    @Override
    public void refresh(Entity subject, String id) {
        Nametag nametag = nametags.get(id);
        if (nametag != null) {
            onMainThread(() -> refresh(subject, nametag));
        }
    }

    @Override
    public void hide(Entity subject) {
        onMainThread(() -> {
            NametagInstance instance = instances.get(subject.getUniqueId());
            if (instance != null) {
                Bukkit.getOnlinePlayers().forEach(instance::hide);
            }
        });
    }

    @Override
    public void show(Entity subject) {
        onMainThread(() -> {
            NametagInstance instance = instances.get(subject.getUniqueId());
            if (instance != null) {
                viewers(subject).forEach(viewer -> attach(instance, viewer));
            }
        });
    }

    private void onMainThread(Runnable task) {
        if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    boolean accepts(Entity subject) {
        return nametags.values().stream().anyMatch(nametag -> nametag.appliesTo(subject));
    }

    boolean selfVisible(Entity subject) {
        return nametags.values().stream()
                .anyMatch(nametag -> nametag.appliesTo(subject) && nametag.selfVisible());
    }

    void attach(Entity subject, Player viewer) {
        attach(instances.computeIfAbsent(subject.getUniqueId(), id -> new NametagInstance(subject)), viewer);
    }

    void detach(Entity subject, Player viewer) {
        NametagInstance instance = instances.get(subject.getUniqueId());
        if (instance != null) {
            instance.hide(viewer);
        }
    }

    void resend(Entity subject) {
        NametagInstance instance = instances.get(subject.getUniqueId());
        if (instance != null) {
            instance.forgetSpawned();
        }
        reattach(subject);
    }

    void reattach(Entity subject) {
        NametagInstance instance = instances.get(subject.getUniqueId());
        if (instance == null) {
            return;
        }
        UUID id = subject.getUniqueId();
        int recovery = recoveries.merge(id, 1, Integer::sum);
        for (long delay : RECOVERY_DELAYS) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (recoveries.getOrDefault(id, recovery) == recovery) {
                    viewers(subject).forEach(viewer -> attach(instance, viewer));
                }
            }, delay);
        }
    }

    void reattachSelf(Player subject, long delay) {
        NametagInstance instance = instances.get(subject.getUniqueId());
        if (instance != null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> attach(instance, subject), delay);
        }
    }

    void cameraChanged(Player viewer, boolean own) {
        onMainThread(() -> {
            if (own) {
                reattachSelf(viewer, CAMERA_RECOVERY_DELAY);
            } else {
                detach(viewer, viewer);
            }
        });
    }

    void fade(Entity subject, boolean faded) {
        NametagInstance instance = instances.get(subject.getUniqueId());
        if (instance == null) {
            return;
        }
        for (Nametag nametag : nametags.values()) {
            Hologram hologram = instance.existing(nametag.id());
            if (hologram == null) {
                continue;
            }
            hologram.edit(display -> {
                if (faded) {
                    display.setTextOpacity(FADED_TEXT_OPACITY);
                    display.setDefaultBackground(false);
                    display.setBackgroundColor(FADED_BACKGROUND);
                } else {
                    display.setTextOpacity((byte) -1);
                    nametag.style(display);
                }
            });
            viewers(subject, nametag).forEach(hologram::refresh);
        }
    }

    void block(Entity subject) {
        if (blocked.add(subject.getUniqueId())) {
            hide(subject);
        }
    }

    void unblock(Entity subject) {
        if (blocked.remove(subject.getUniqueId())) {
            reattach(subject);
        }
    }

    void died(Entity subject) {
        dead.add(subject.getUniqueId());
        block(subject);
    }

    void respawned(Entity subject) {
        dead.remove(subject.getUniqueId());
        unblock(subject);
    }

    void sweepRevived() {
        dead.removeIf(id -> {
            Player player = Bukkit.getPlayer(id);
            if (player == null) {
                blocked.remove(id);
                return true;
            }
            if (player.isDead()) {
                return false;
            }
            unblock(player);
            return true;
        });
    }

    int[] mergePassengers(int vehicleId, Player viewer, int[] passengers) {
        NametagInstance instance = instanceOf(vehicleId);
        if (instance == null || blocked.contains(instance.subject().getUniqueId())) {
            return null;
        }
        List<Integer> ours = new ArrayList<>();
        for (Nametag nametag : shownTo(instance.subject(), viewer)) {
            Hologram hologram = instance.existing(nametag.id());
            if (hologram != null) {
                ours.add(hologram.entityId());
            }
        }
        if (ours.isEmpty()) {
            return null;
        }
        List<Integer> merged = new ArrayList<>(passengers.length + ours.size());
        for (int passenger : passengers) {
            if (!ours.contains(passenger)) {
                merged.add(passenger);
            }
        }
        merged.addAll(ours);
        if (merged.size() == passengers.length && matches(merged, passengers)) {
            return null;
        }
        return merged.stream().mapToInt(Integer::intValue).toArray();
    }

    private static boolean matches(List<Integer> merged, int[] passengers) {
        for (int index = 0; index < passengers.length; index++) {
            if (merged.get(index) != passengers[index]) {
                return false;
            }
        }
        return true;
    }

    private NametagInstance instanceOf(int entityId) {
        return instances.values().stream()
                .filter(instance -> instance.subject().getEntityId() == entityId)
                .findFirst()
                .orElse(null);
    }

    void forget(Entity subject) {
        blocked.remove(subject.getUniqueId());
        dead.remove(subject.getUniqueId());
        recoveries.remove(subject.getUniqueId());
        NametagInstance instance = instances.remove(subject.getUniqueId());
        if (instance != null) {
            Bukkit.getOnlinePlayers().forEach(instance::hide);
        }
    }

    private void refresh(Entity subject, Nametag nametag) {
        NametagInstance instance = instances.get(subject.getUniqueId());
        if (instance == null) {
            return;
        }
        Hologram hologram = instance.existing(nametag.id());
        if (hologram == null) {
            return;
        }
        viewers(subject, nametag).forEach(viewer -> hologram.update(viewer, nametag.text(subject, viewer)));
    }

    private void attach(NametagInstance instance, Player viewer) {
        if (blocked.contains(instance.subject().getUniqueId())) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (blocked.contains(instance.subject().getUniqueId())) {
                return;
            }
            List<Nametag> shown = shownTo(instance.subject(), viewer);
            if (!shown.isEmpty()) {
                NametagInstance.send(viewer, instance.attach(shown, viewer));
            }
        }, ATTACH_DELAY);
    }

    private List<Nametag> shownTo(Entity subject, Player viewer) {
        return nametags.values().stream()
                .filter(nametag -> nametag.appliesTo(subject))
                .filter(nametag -> viewer != subject || nametag.selfVisible())
                .toList();
    }

    private Collection<Player> viewers(Entity subject) {
        List<Player> viewers = new ArrayList<>(subject.getTrackedBy());
        if (subject instanceof Player self && selfVisible(subject)) {
            viewers.add(self);
        }
        return viewers;
    }

    private Collection<Player> viewers(Entity subject, Nametag nametag) {
        List<Player> viewers = new ArrayList<>(subject.getTrackedBy());
        if (nametag.selfVisible() && subject instanceof Player self) {
            viewers.add(self);
        }
        return viewers;
    }
}
