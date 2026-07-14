package kr.junhyung.mainframe.platform.paper.plugin;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler;
import io.papermc.paper.plugin.lifecycle.event.registrar.RegistrarEvent;
import org.bukkit.plugin.Plugin;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;

@AutoConfiguration
public class MainframePluginAutoConfiguration {

    @ConditionalOnExpression("'cat' == 'dog'")
    @Bean
    public Plugin plugin() {
        throw new UnsupportedOperationException();
    }

    @Bean(destroyMethod = "")
    public LifecycleEventManager<Plugin> lifecycleEventManager(Plugin plugin) {
        return plugin.getLifecycleManager();
    }

    @MainThread
    @Bean(defaultCandidate = false)
    public AsyncTaskExecutor mainThreadTaskExecutor(Plugin plugin) {
        return new MainThreadTaskExecutor(plugin);
    }

    @Bean
    static EventHandlerRegistrar eventHandlerRegistrar(Plugin plugin) {
        return new EventHandlerRegistrar(plugin);
    }

    @Bean
    static ServiceRegistrar serviceRegistrar(Plugin plugin) {
        return new ServiceRegistrar(plugin);
    }

    @Bean
    LifecycleEventHandlerRegistrar lifecycleEventHandlerRegistrar(ObjectProvider<LifecycleEventHandler<RegistrarEvent<?>>> handlers,
                                                                  LifecycleEventManager<Plugin> lifecycleManager) {
        return new LifecycleEventHandlerRegistrar(handlers, lifecycleManager);
    }

}