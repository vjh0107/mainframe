package kr.junhyung.mainframe.paper.condition

import org.apache.maven.artifact.versioning.ComparableVersion
import org.bukkit.Bukkit
import org.springframework.boot.autoconfigure.condition.ConditionOutcome
import org.springframework.boot.autoconfigure.condition.SpringBootCondition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.type.AnnotatedTypeMetadata

@Order(Ordered.LOWEST_PRECEDENCE)
internal class OnMinecraftVersionCondition : SpringBootCondition() {

    override fun getMatchOutcome(
        context: ConditionContext,
        metadata: AnnotatedTypeMetadata
    ): ConditionOutcome {
        val attributes = metadata.getAnnotationAttributes(ConditionalOnMinecraftVersion::class.java.name)
        if (attributes == null) {
            return ConditionOutcome(false, "No attributes found")
        }
        val expectedVersion = attributes["version"] as? String ?: error("Expected version is null")
        val currentVersion = Bukkit.getMinecraftVersion()

        val expectedComparableVersion = ComparableVersion(expectedVersion)
        val currentComparableVersion = ComparableVersion(currentVersion)

        return ConditionOutcome(
            currentComparableVersion >= expectedComparableVersion,
            "Minecraft version is $currentVersion, expected $expectedVersion"
        )
    }

}