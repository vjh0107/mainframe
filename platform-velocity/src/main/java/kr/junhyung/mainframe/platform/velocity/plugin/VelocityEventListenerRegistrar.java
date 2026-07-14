package kr.junhyung.mainframe.platform.velocity.plugin;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jspecify.annotations.NonNull;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

class VelocityEventListenerRegistrar implements BeanPostProcessor {

    private final ProxyServer proxyServer;
    private final Object plugin;

    VelocityEventListenerRegistrar(ProxyServer proxyServer, PluginContainer pluginContainer) {
        this.proxyServer = proxyServer;
        this.plugin = pluginContainer.getInstance()
            .orElseThrow(() -> new IllegalStateException("Plugin instance is not available"));
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (!AnnotationUtils.isCandidateClass(targetClass, Subscribe.class)) {
            return bean;
        }
        boolean listener = !MethodIntrospector.selectMethods(targetClass,
            (ReflectionUtils.MethodFilter) method -> AnnotatedElementUtils.isAnnotated(method, Subscribe.class)).isEmpty();
        if (listener) {
            proxyServer.getEventManager().register(plugin, bean);
        }
        return bean;
    }

}
