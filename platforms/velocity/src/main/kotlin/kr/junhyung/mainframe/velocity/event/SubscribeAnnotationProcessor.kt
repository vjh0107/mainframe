package kr.junhyung.mainframe.velocity.event

import com.velocitypowered.api.event.AwaitingEventExecutor
import com.velocitypowered.api.event.EventHandler
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.EventTask
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.plugin.PluginContainer
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kr.junhyung.mainframe.core.annotation.MethodAnnotationProcessor
import kr.junhyung.mainframe.core.util.stdlib.uncheckedCast
import org.springframework.context.ApplicationContext
import org.springframework.core.MethodParameter
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.reflect.full.callSuspend
import kotlin.reflect.jvm.kotlinFunction

internal class SubscribeAnnotationProcessor(
    private val applicationContext: ApplicationContext,
    private val pluginContainer: PluginContainer,
    private val eventManager: EventManager
) : MethodAnnotationProcessor<Subscribe>() {

    private val coroutineScope: CoroutineScope by lazy {
        applicationContext.getBean(CoroutineScope::class.java)
            ?: error("CoroutineScope bean is not available in the application context")
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String, method: Method, annotation: Subscribe) {
        val methodParameter = MethodParameter(method, 0)
            .withContainingClass(bean::class.java)
            .parameterType

        val eventExecutor = create(bean, method)
        registerEvent(methodParameter, annotation, eventExecutor)
    }

    private fun create(instance: Any, method: Method): EventHandler<*> {
        return AwaitingEventExecutor<Any> { event ->
            EventTask.withContinuation { continuation ->
                val kotlinFunction = method.kotlinFunction
                if (kotlinFunction?.isSuspend != true) {
                    try {
                        method.invoke(instance, event)
                        continuation.resume()
                    } catch (invocationTargetException: InvocationTargetException) {
                        val cause = invocationTargetException.cause
                        if (cause == null) {
                            continuation.resumeWithException(invocationTargetException)
                            return@withContinuation
                        }
                        continuation.resumeWithException(cause)
                    } catch (throwable: Throwable) {
                        continuation.resumeWithException(throwable)
                    }
                    return@withContinuation
                }

                coroutineScope.launch {
                    try {
                        kotlinFunction.callSuspend(instance, event)
                    } catch (invocationTargetException: InvocationTargetException) {
                        val cause = invocationTargetException.cause
                        if (cause == null) {
                            coroutineContext[CoroutineExceptionHandler]?.handleException(coroutineContext, invocationTargetException)
                            return@launch
                        }
                        coroutineContext[CoroutineExceptionHandler]?.handleException(coroutineContext, cause)
                    } catch (throwable: Throwable) {
                        coroutineContext[CoroutineExceptionHandler]?.handleException(coroutineContext, throwable)
                    } finally {
                        continuation.resume()
                    }
                }
            }
        }
    }

    private fun registerEvent(
        eventType: Class<*>,
        subscribeAnnotation: Subscribe,
        eventHandler: EventHandler<*>
    ) {
        eventManager.register(pluginContainer, eventType.uncheckedCast(), subscribeAnnotation.priority, eventHandler)
    }

}