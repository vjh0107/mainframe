package kr.junhyung.mainframe.platform.paper.nametag.modelengine;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.events.AddModelEvent;
import com.ticxo.modelengine.api.events.RemoveModelEvent;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PivotOverride;
import kr.junhyung.mainframe.platform.paper.nametag.NametagPassengers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

class ModelEngineNametagMount implements Listener {

    private final NametagPassengers passengers;
    private final Plugin plugin;

    ModelEngineNametagMount(NametagPassengers passengers, Plugin plugin) {
        this.passengers = passengers;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAddModel(AddModelEvent event) {
        Entity subject = subjectOf(event.getTarget());
        if (subject != null) {
            Bukkit.getScheduler().runTask(plugin, () -> attach(subject));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRemoveModel(RemoveModelEvent event) {
        Entity subject = subjectOf(event.getTarget());
        if (subject != null) {
            Bukkit.getScheduler().runTask(plugin, () -> detach(subject));
        }
    }

    private Entity subjectOf(ModeledEntity modeled) {
        return modeled == null || modeled.getBase() == null ? null
                : Bukkit.getEntity(modeled.getBase().getUUID());
    }

    private void attach(Entity subject) {
        override(subject).ifPresent(override -> passengers.of(subject).forEach(override::addPassenger));
    }

    private void detach(Entity subject) {
        passengers.remount(subject);
    }

    private Optional<PivotOverride> override(Entity subject) {
        return ModelEngineAPI.getPivotOverrideRegistry().get(subject.getUniqueId());
    }
}
