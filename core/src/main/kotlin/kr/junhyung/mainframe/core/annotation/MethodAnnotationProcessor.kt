package kr.junhyung.mainframe.core.annotation

import kr.junhyung.mainframe.core.util.stdlib.uncheckedCast
import org.springframework.aop.framework.AopInfrastructureBean
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.core.MethodIntrospector
import org.springframework.core.ResolvableType
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.annotation.AnnotationUtils
import java.lang.reflect.Method

public abstract class MethodAnnotationProcessor<T : Annotation> : BeanPostProcessor {

    private val annotationClass: Class<T> by lazy {
        ResolvableType
            .forClass(MethodAnnotationProcessor::class.java, this::class.java)
            .getGeneric(0)
            .resolve(Annotation::class.java)
            .uncheckedCast()
    }

    protected open fun postProcessBeforeInitialization(
        bean: Any,
        beanName: String,
        method: Method,
        annotation: T
    ) {}

    protected open fun postProcessAfterInitialization(
        bean: Any,
        beanName: String,
        method: Method,
        annotation: T
    ) {}


    final override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        val methods = selectMethods(bean)
        if (methods.isEmpty()) {
            return bean
        }
        methods.forEach { method ->
            val annotation = AnnotatedElementUtils.getMergedAnnotation(method, annotationClass)
                ?: error("Method $method is expected to be annotated with ${annotationClass.simpleName} but not found")
            postProcessAfterInitialization(bean, beanName, method, annotation)
        }
        return bean
    }

    final override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        val methods = selectMethods(bean)
        if (methods.isEmpty()) {
            return bean
        }
        methods.forEach { method ->
            val annotation = AnnotatedElementUtils.getMergedAnnotation(method, annotationClass)
                ?: error("Method $method is expected to be annotated with ${annotationClass.simpleName} but not found")
            postProcessBeforeInitialization(bean, beanName, method, annotation)
        }
        return bean
    }

    private fun selectMethods(bean: Any): Set<Method> {
        if (bean is AopInfrastructureBean) {
            return emptySet()
        }
        val targetClass = bean::class.java
        if (!AnnotationUtils.isCandidateClass(targetClass, annotationClass)) {
            return emptySet()
        }
        return MethodIntrospector.selectMethods(targetClass) { method ->
            AnnotatedElementUtils.isAnnotated(method, annotationClass)
        }
    }

}