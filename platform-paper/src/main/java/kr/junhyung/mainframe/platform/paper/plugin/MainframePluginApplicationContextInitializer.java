package kr.junhyung.mainframe.platform.paper.plugin;

import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

public class MainframePluginApplicationContextInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    private final Plugin plugin;

    MainframePluginApplicationContextInitializer(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize(@NonNull GenericApplicationContext applicationContext) {
        SingletonBeanRegistry registry = applicationContext.getBeanFactory();
        registry.registerSingleton("plugin", plugin);
    }

}