package kr.junhyung.mainframe.platform.velocity.plugin;

import com.velocitypowered.api.command.CommandManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;

class VelocityCommandRegistrar implements SmartInitializingSingleton {

    private final ObjectProvider<VelocityCommand> commands;
    private final CommandManager commandManager;

    VelocityCommandRegistrar(ObjectProvider<VelocityCommand> commands, CommandManager commandManager) {
        this.commands = commands;
        this.commandManager = commandManager;
    }

    @Override
    public void afterSingletonsInstantiated() {
        commands.forEach(command -> commandManager.register(command.meta(), command.command()));
    }

}
