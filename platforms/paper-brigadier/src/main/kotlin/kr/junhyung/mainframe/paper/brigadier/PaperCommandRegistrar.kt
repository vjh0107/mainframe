package kr.junhyung.mainframe.paper.brigadier

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import kr.junhyung.mainframe.brigadier.Command
import kr.junhyung.mainframe.brigadier.CommandRegistrar
import org.bukkit.command.CommandSender
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
            val delegatedCommand = DelegatedCommand(command, metadata.permission)
            event.registrar().register(metadata.name, metadata.description, metadata.aliases.toList(), delegatedCommand)
        }
    }

    @Suppress("UnstableApiUsage")
    private class DelegatedCommand(
        private val delegate: BasicCommand,
        private val permission: String
    ) : BasicCommand by delegate {

        override fun suggest(
            commandSourceStack: CommandSourceStack,
            args: Array<out String>
        ): Collection<String> {
            return delegate.suggest(commandSourceStack, args)
        }

        override fun canUse(sender: CommandSender): Boolean {
            return delegate.canUse(sender)
        }

        override fun permission(): String? {
            return permission
        }

    }

}