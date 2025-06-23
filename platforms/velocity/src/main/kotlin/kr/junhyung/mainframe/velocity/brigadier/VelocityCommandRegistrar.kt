package kr.junhyung.mainframe.velocity.brigadier

import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.Command as VelocityCommand
import kr.junhyung.mainframe.brigadier.Command
import kr.junhyung.mainframe.brigadier.CommandRegistrar
import org.slf4j.LoggerFactory
import kotlin.jvm.java

internal class VelocityCommandRegistrar(
    private val commandManager: CommandManager,
    private val plugin: Any
) : CommandRegistrar {

    private val logger = LoggerFactory.getLogger(VelocityCommandRegistrar::class.java)

    override fun registerCommand(command: Any, metadata: Command) {
        if (command !is VelocityCommand) {
            throw UnsupportedOperationException("Command annotation can only be used on Command, but found ${command::class.java.name}")
        }
        val commandMeta = commandManager.metaBuilder(metadata.name)
            .aliases(*metadata.aliases)
            .plugin(plugin)
            .build()
        commandManager.register(commandMeta, command)
        logger.debug("Registered command: ${metadata.name} with aliases: ${metadata.aliases.joinToString(", ")}")
    }
}