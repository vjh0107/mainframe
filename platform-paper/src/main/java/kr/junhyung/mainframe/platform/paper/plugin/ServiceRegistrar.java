package kr.junhyung.mainframe.platform.paper.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jspecify.annotations.NonNull;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Service;

import java.util.List;

class ServiceRegistrar implements BeanPostProcessor {

    private final Plugin plugin;

    public ServiceRegistrar(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (!AnnotatedElementUtils.hasAnnotation(targetClass, Service.class)) {
            return bean;
        }
        ServicesManager servicesManager = Bukkit.getServicesManager();
        for (Class<?> serviceType : serviceTypes(targetClass)) {
            register(servicesManager, serviceType, bean);
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    private <T> void register(ServicesManager servicesManager, Class<?> serviceType, Object bean) {
        servicesManager.register((Class<T>) serviceType, (T) bean, plugin, ServicePriority.Normal);
    }

    private static List<Class<?>> serviceTypes(Class<?> targetClass) {
        Class<?>[] interfaces = targetClass.getInterfaces();
        return interfaces.length == 0 ? List.of(targetClass) : List.of(interfaces);
    }
}
