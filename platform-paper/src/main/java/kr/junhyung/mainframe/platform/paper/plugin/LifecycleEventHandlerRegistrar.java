package kr.junhyung.mainframe.platform.paper.plugin;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler;
import io.papermc.paper.plugin.lifecycle.event.registrar.RegistrarEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.ResolvableType;

public class LifecycleEventHandlerRegistrar implements SmartInitializingSingleton {

    private final ObjectProvider<LifecycleEventHandler<RegistrarEvent<?>>> handlers;
    private final LifecycleEventManager<Plugin> lifecycleManager;

    public LifecycleEventHandlerRegistrar(ObjectProvider<LifecycleEventHandler<RegistrarEvent<?>>> handlers,
                                          LifecycleEventManager<Plugin> lifecycleManager) {
        this.handlers = handlers;
        this.lifecycleManager = lifecycleManager;
    }

    @Override
    public void afterSingletonsInstantiated() {
        handlers.forEach(this::register);
    }

    private void register(LifecycleEventHandler<RegistrarEvent<?>> handler) {
        Class<?> registrar = resolveRegistrarType(handler);
        if (registrar == Commands.class) {
            lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, handler);
        } else {
            throw new IllegalStateException("No lifecycle event mapping for registrar '" + registrar.getName()
                    + "' (handler: " + handler.getClass().getName() + ")");
        }
    }

    private Class<?> resolveRegistrarType(LifecycleEventHandler<RegistrarEvent<?>> handler) {
        Class<?> registrar = ResolvableType.forInstance(handler)
                .as(LifecycleEventHandler.class) // LifecycleEventHandler<RegistrarEvent<T>>
                .getGeneric(0)                   // RegistrarEvent<T>
                .getGeneric(0)                   // T
                .resolve();
        if (registrar == null) {
            throw new IllegalStateException("Could not resolve the registrar generic of "
                    + handler.getClass().getName() + "; declare it explicitly, e.g. LifecycleEventHandler<RegistrarEvent<Commands>>");
        }
        return registrar;
    }

}
