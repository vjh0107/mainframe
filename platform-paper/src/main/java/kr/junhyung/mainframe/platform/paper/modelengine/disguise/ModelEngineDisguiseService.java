package kr.junhyung.mainframe.platform.paper.modelengine.disguise;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PivotOverride;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.Head;
import com.ticxo.modelengine.api.model.bone.type.PlayerLimb;
import com.ticxo.modelengine.api.nms.entity.wrapper.TrackedEntity;
import kr.junhyung.mainframe.platform.paper.disguise.DisguisePassengers;
import kr.junhyung.mainframe.platform.paper.disguise.DisguiseService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModelEngineDisguiseService implements DisguiseService {

    private final List<DisguisePassengers> passengers;
    private final Map<Integer, Entity> disguised = new ConcurrentHashMap<>();

    public ModelEngineDisguiseService(List<DisguisePassengers> passengers) {
        this.passengers = passengers;
    }

    @Override
    public void apply(Entity subject, String model) {
        ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(model);
        if (blueprint == null) {
            throw new IllegalArgumentException("Unknown model: " + model);
        }
        ModeledEntity modeled = ModelEngineAPI.getOrCreateModeledEntity(subject);
        modeled.restore();
        if (subject instanceof Player) {
            modeled.getBase().getBodyRotationController().setPlayerMode(true);
        }
        PivotOverride override = ModelEngineAPI.getPivotOverrideRegistry().getOrCreate(subject);
        modeled.setBaseEntityVisible(false);
        ModelEngineAPI.getEntityHandler().setForcedInvisible(subject, true);
        tracked(modeled).addForcedPairing(subject.getUniqueId());
        disguised.put(subject.getEntityId(), subject);
        if (modeled.getModel(model).isEmpty()) {
            ActiveModel active = ModelEngineAPI.createActiveModel(blueprint);
            modeled.addModel(active, false).ifPresent(ActiveModel::destroy);
            active.setPivotOverride(override);
            configureBones(active, subject);
        }
        mount(subject, override);
    }

    @Override
    public void clear(Entity subject, String model) {
        ModeledEntity modeled = ModelEngineAPI.getModeledEntity(subject.getUniqueId());
        if (modeled == null) {
            return;
        }
        modeled.removeModel(model).ifPresent(ActiveModel::destroy);
        if (!modeled.getModels().isEmpty()) {
            return;
        }
        tracked(modeled).removeForcedPairing(subject.getUniqueId());
        disguised.remove(subject.getEntityId());
        modeled.setBaseEntityVisible(true);
        modeled.markRemoved();
        ModelEngineAPI.getEntityHandler().setForcedInvisible(subject, false);
        passengers.forEach(source -> source.remount(subject));
    }

    @Override
    public boolean isDisguised(Entity subject) {
        ModeledEntity modeled = ModelEngineAPI.getModeledEntity(subject.getUniqueId());
        return modeled != null && !modeled.getModels().isEmpty();
    }

    @Override
    public boolean isDisguised(Entity subject, String model) {
        ModeledEntity modeled = ModelEngineAPI.getModeledEntity(subject.getUniqueId());
        return modeled != null && modeled.getModel(model).isPresent();
    }

    Entity disguised(int entityId) {
        return disguised.get(entityId);
    }

    private void mount(Entity subject, PivotOverride override) {
        for (DisguisePassengers source : passengers) {
            source.of(subject).forEach(override::addPassenger);
        }
    }

    private void configureBones(ActiveModel model, Entity subject) {
        for (ModelBone bone : model.getBones().values()) {
            bone.getBoneBehavior(BoneBehaviorTypes.HEAD).ifPresent(head -> ((Head) head).setLocal(true));
            if (subject instanceof Player player) {
                bone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB)
                        .ifPresent(limb -> ((PlayerLimb) limb).setTexture(player));
            }
        }
    }

    private TrackedEntity tracked(ModeledEntity modeled) {
        return ((BukkitEntityData) modeled.getBase().getData()).getTracked();
    }
}
