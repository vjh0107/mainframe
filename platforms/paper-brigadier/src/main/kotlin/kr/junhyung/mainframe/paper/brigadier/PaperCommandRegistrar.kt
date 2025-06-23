package kr.junhyung.mainframe.paper.brigadier

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import kr.junhyung.mainframe.brigadier.Command
import kr.junhyung.mainframe.brigadier.CommandRegistrar
import org.bukkit.plugin.Plugin

internal class PaperCommandRegistrar(
    private val plugin: Plugin
) : CommandRegistrar {

    @Suppress("UnstableApiUsage")
    override fun registerCommand(command: Any, metadata: Command) {
        if (command !is BasicCommand) {
            throw UnsupportedOperationException("Command annotation can only be used on BasicCommand, but found ${command::class.java.name}")
        }
        plugin.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            event.registrar().register(metadata.name, metadata.description, metadata.aliases.toList(), command)
        }
    }

}