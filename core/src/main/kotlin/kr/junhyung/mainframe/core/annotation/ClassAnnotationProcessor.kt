package kr.junhyung.mainframe.core.annotation

import kr.junhyung.mainframe.core.util.stdlib.uncheckedCast
import org.springframework.aop.framework.AopInfrastructureBean
import org.springframework.aop.framework.AopProxyUtils
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.core.ResolvableType
import org.springframework.core.annotation.AnnotationUtils
import kotlin.jvm.java

public abstract class ClassAnnotationProcessor<T : Annotation> : BeanPostProcessor {

    private val annotationClass: Class<T> by lazy {
        ResolvableType
            .forClass(ClassAnnotationProcessor::class.java, this::class.java)
            .getGeneric(0)
            .resolve(Annotation::class.java)
            .uncheckedCast()
    }

    protected open fun postProcessAfterInitialization(bean: Any, beanName: String, annotation: T) {}

    protected open fun postProcessBeforeInitialization(bean: Any, beanName: String, annotation: T) {}

    final override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val annotation = resolveAnnotation(bean) ?: return bean
        postProcessAfterInitialization(bean, beanName, annotation)
        return bean
    }

    final override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val annotation = resolveAnnotation(bean) ?: return bean
        postProcessBeforeInitialization(bean, beanName, annotation)
        return bean
    }

    private fun resolveAnnotation(bean: Any): T? {
        if (bean is AopInfrastructureBean) {
            return null
        }
        val ultimateTargetClass = AopProxyUtils.ultimateTargetClass(bean)
        if (!AnnotationUtils.isCandidateClass(ultimateTargetClass, annotationClass)) {
            return null
        }
        val annotation = AnnotationUtils.findAnnotation(ultimateTargetClass, annotationClass)
        if (annotation == null) {
            return null
        }
        return annotation
    }

}