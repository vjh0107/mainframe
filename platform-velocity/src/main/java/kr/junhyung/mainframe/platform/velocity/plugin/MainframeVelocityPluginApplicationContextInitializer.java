package kr.junhyung.mainframe.platform.velocity.plugin;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

import java.nio.file.Path;

public class MainframeVelocityPluginApplicationContextInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    private final Object plugin;
    private final ProxyServer proxyServer;
    private final PluginContainer pluginContainer;
    private final Logger logger;
    private final Path dataDirectory;

    MainframeVelocityPluginApplicationContextInitializer(Object plugin,
                                                         ProxyServer proxyServer,
                                                         PluginContainer pluginContainer,
                                                         Logger logger,
                                                         Path dataDirectory) {
        this.plugin = plugin;
        this.proxyServer = proxyServer;
        this.pluginContainer = pluginContainer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Override
    public void initialize(@NonNull GenericApplicationContext applicationContext) {
        SingletonBeanRegistry registry = applicationContext.getBeanFactory();
        registry.registerSingleton("plugin", plugin);
        registry.registerSingleton("proxyServer", proxyServer);
        registry.registerSingleton("pluginContainer", pluginContainer);
        registry.registerSingleton("logger", logger);
        registry.registerSingleton("dataDirectory", dataDirectory);
    }

}
