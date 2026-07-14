package kr.junhyung.mainframe.platform.paper.plugin;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Set;

class EventHandlerRegistrar implements BeanPostProcessor {

    private final Plugin plugin;

    public EventHandlerRegistrar(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (!AnnotationUtils.isCandidateClass(targetClass, EventHandler.class)) {
            return bean;
        }
        Set<Method> methods = MethodIntrospector.selectMethods(targetClass,
            (ReflectionUtils.MethodFilter) method -> AnnotatedElementUtils.isAnnotated(method, EventHandler.class));
        if (methods.isEmpty()) {
            return bean;
        }
        if (!(bean instanceof Listener listener)) {
            throw new BeanCreationException("Bean is not a Listener: " + bean.getClass().getName());
        }
        for (Method method : methods) {
            registerHandler(listener, method);
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    private void registerHandler(Listener bean, Method method) {
        EventHandler annotation = AnnotatedElementUtils.findMergedAnnotation(method, EventHandler.class);
        if (annotation == null) {
            throw new BeanCreationException("EventHandler annotation is not present: " + bean.getClass().getName() + "#" + method.getName());
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0 || !Event.class.isAssignableFrom(parameterTypes[0])) {
            throw new BeanCreationException("first parameter is not an subtype of Event: " + bean.getClass().getName() + "#" + method.getName());
        }
        Class<? extends Event> eventClass = (Class<? extends Event>) parameterTypes[0];
        EventExecutor executor = EventExecutor.create(method, eventClass);
        Bukkit.getPluginManager().registerEvent(eventClass, bean, annotation.priority(), executor, plugin, annotation.ignoreCancelled());
    }

}
