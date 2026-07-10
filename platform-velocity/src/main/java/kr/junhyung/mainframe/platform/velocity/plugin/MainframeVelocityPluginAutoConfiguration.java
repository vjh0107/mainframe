package kr.junhyung.mainframe.platform.velocity.plugin;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class MainframeVelocityPluginAutoConfiguration {

    @Bean(destroyMethod = "")
    public CommandManager commandManager(ProxyServer proxyServer) {
        return proxyServer.getCommandManager();
    }

    @Bean
    static VelocityEventListenerRegistrar velocityEventListenerRegistrar(ProxyServer proxyServer, PluginContainer pluginContainer) {
        return new VelocityEventListenerRegistrar(proxyServer, pluginContainer);
    }

    @Bean
    VelocityCommandRegistrar velocityCommandRegistrar(ObjectProvider<VelocityCommand> commands, CommandManager commandManager) {
        return new VelocityCommandRegistrar(commands, commandManager);
    }

}
