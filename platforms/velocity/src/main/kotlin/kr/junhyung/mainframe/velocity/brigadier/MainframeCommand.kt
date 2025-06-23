package kr.junhyung.mainframe.velocity.brigadier

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.command.SimpleCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import kotlinx.coroutines.launch
import kr.junhyung.mainframe.brigadier.Command
import kr.junhyung.mainframe.brigadier.CommandAnnotatedElement
import kr.junhyung.mainframe.core.coroutines.CoroutineScopeAware
import java.util.concurrent.CompletableFuture

public abstract class MainframeCommand : SimpleCommand, CommandAnnotatedElement, CoroutineScopeAware {

    public lateinit var name: String
    public lateinit var aliases: List<String>
    public lateinit var permission: String
    public lateinit var description: String
    public lateinit var usageMessage: String
    private lateinit var coroutineScope: CoroutineScope

    final override fun setCommand(command: Command) {
        this.name = command.name
        this.aliases = command.aliases.toList()
        this.permission = command.permission
        this.description = command.description
        this.usageMessage = command.usageMessage
    }

    final override fun setCoroutineScope(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
    }

    public open suspend fun execute(source: CommandSource, label: String, arguments: List<String>) {
    }

    final override fun execute(invocation: SimpleCommand.Invocation) {
        coroutineScope.launch {
            execute(invocation.source(), invocation.alias(), invocation.arguments().toList())
        }
    }

    public open suspend fun completions(source: CommandSource, label: String, arguments: List<String>): List<String> {
        return emptyList()
    }

    final override fun suggestAsync(invocation: SimpleCommand.Invocation): CompletableFuture<List<String>> {
        return coroutineScope.future {
            completions(invocation.source(), invocation.alias(), invocation.arguments().toList())
        }
    }

    final override fun hasPermission(invocation: SimpleCommand.Invocation): Boolean {
        return hasPermission(invocation.source(), invocation.alias(), invocation.arguments().toList())
    }

    public open fun hasPermission(source: CommandSource, label: String, arguments: List<String>): Boolean {
        if (permission.isBlank()) {
            return true
        }
        return source.hasPermission(permission)
    }

    public fun sendUsageMessage(commandSource: CommandSource) {
        commandSource.sendRichMessage(usageMessage)
    }

    final override fun suggest(invocation: SimpleCommand.Invocation): List<String> {
        error("This method should not be called. Use completions instead.")
    }

}