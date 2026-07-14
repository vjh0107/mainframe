package kr.junhyung.mainframe.platform.paper.condition;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OnPluginCondition extends SpringBootCondition {

    @Override
    public @NonNull ConditionOutcome getMatchOutcome(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        final var attributes = metadata.getAnnotationAttributes(ConditionalOnPlugin.class.getName());
        if (attributes == null) {
            return new ConditionOutcome(false, "No attributes found");
        }
        String dependPlugin = (String) attributes.get("plugin");
        if (dependPlugin == null) {
            return new ConditionOutcome(false, "Plugin name is null");
        }
        Plugin plugin = Bukkit.getPluginManager().getPlugin(dependPlugin);
        if (plugin == null) {
            return new ConditionOutcome(false, "Plugin not loaded: " + dependPlugin);
        }
        return new ConditionOutcome(true, "Plugin loaded: " + dependPlugin);
    }

}
