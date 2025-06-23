package kr.junhyung.mainframe.paper.event

import kr.junhyung.mainframe.core.annotation.MethodAnnotationProcessor
import kr.junhyung.mainframe.core.util.stdlib.uncheckedCast
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager
import org.springframework.core.MethodParameter
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

internal class EventHandlerAnnotationProcessor(
    private val plugin: Plugin,
    private val pluginManager: PluginManager = Bukkit.getPluginManager()
) : MethodAnnotationProcessor<EventHandler>() {

    override fun postProcessAfterInitialization(bean: Any, beanName: String, method: Method, annotation: EventHandler) {
        val methodParameter = MethodParameter(method, 0)
            .withContainingClass(bean::class.java)
            .parameterType
        check(Event::class.java.isAssignableFrom(methodParameter)) {
            "first parameter of event handler must be a subclass of Event."
        }
        val eventExecutor = create(bean, method, methodParameter)
        registerEvent(eventExecutor, methodParameter, annotation)
    }

    private fun create(listener: Any, method: Method, targetEventClass: Class<*>): EventExecutor {
        return EventExecutor { _, event ->
            if (!targetEventClass.isInstance(event)) {
                return@EventExecutor
            }
            try {
                method.invoke(listener, event)
            } catch (invocationTargetException: InvocationTargetException) {
                val cause = invocationTargetException.cause ?: throw invocationTargetException
                throw cause
            }
        }
    }

    private fun registerEvent(
        eventExecutor: EventExecutor,
        eventType: Class<*>,
        eventHandler: EventHandler
    ) {
        pluginManager.registerEvent(
            eventType.uncheckedCast(),
            object : Listener {},
            eventHandler.priority,
            eventExecutor,
            plugin
        )
    }

}