package kr.junhyung.mainframe.paper.condition

import org.bukkit.Bukkit
import org.springframework.boot.autoconfigure.condition.ConditionOutcome
import org.springframework.boot.autoconfigure.condition.SpringBootCondition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.type.AnnotatedTypeMetadata

@Order(Ordered.LOWEST_PRECEDENCE)
internal class OnPluginCondition : SpringBootCondition() {

    override fun getMatchOutcome(
        context: ConditionContext,
        metadata: AnnotatedTypeMetadata
    ): ConditionOutcome {
        val attributes = metadata.getAnnotationAttributes(ConditionalOnPlugin::class.java.name)
        if (attributes == null) {
            return ConditionOutcome(false, "No attributes found")
        }
        val dependPlugin = attributes["plugin"]
        if (dependPlugin == null || dependPlugin as? String == null) {
            return ConditionOutcome(false, "Plugin name is null")
        }
        val pluginInstance = Bukkit.getPluginManager().getPlugin(dependPlugin)
        if (pluginInstance == null) {
            return ConditionOutcome(false, "Plugin $dependPlugin is not loaded")
        }
        return ConditionOutcome(true, "Plugin $dependPlugin is loaded")
    }

}