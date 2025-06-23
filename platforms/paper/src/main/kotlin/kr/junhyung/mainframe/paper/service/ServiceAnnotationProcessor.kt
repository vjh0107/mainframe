package kr.junhyung.mainframe.paper.service

import kr.junhyung.mainframe.core.annotation.ClassAnnotationProcessor
import kr.junhyung.mainframe.core.util.stdlib.uncheckedCast
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.ServicesManager
import org.springframework.aop.framework.AopProxyUtils
import org.springframework.stereotype.Service

internal class ServiceAnnotationProcessor(
    private val plugin: Plugin,
    private val servicesManager: ServicesManager = Bukkit.getServicesManager()
) : ClassAnnotationProcessor<Service>() {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String, annotation: Service) {
        val ultimateTargetClass = AopProxyUtils.ultimateTargetClass(bean)
        val binds = mutableListOf<Class<*>>()
        binds.add(ultimateTargetClass)
        ultimateTargetClass.interfaces.forEach { bindClass ->
            binds.add(bindClass)
        }
        binds.forEach { bindClass ->
            servicesManager.register<Any>(
                bindClass.uncheckedCast(),
                bean,
                plugin,
                ServicePriority.Normal
            )
        }
    }

}