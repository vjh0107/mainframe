package kr.junhyung.mainframe.core.coroutines

import kotlinx.coroutines.CoroutineScope
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.util.function.SingletonSupplier

internal class CoroutineScopeAwareBeanPostProcessor(private val applicationContext: ApplicationContext) : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        if (bean is CoroutineScopeAware) {
            bean.setCoroutineScope(getCoroutineScope())
        }
        return bean
    }

    private fun getCoroutineScope(): CoroutineScope {
        return SingletonSupplier.of {
            applicationContext.getBean(CoroutineScope::class.java)
        }.obtain()
    }

}