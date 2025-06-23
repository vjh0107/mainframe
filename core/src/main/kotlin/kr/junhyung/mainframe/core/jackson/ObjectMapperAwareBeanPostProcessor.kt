package kr.junhyung.mainframe.core.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.util.function.SingletonSupplier

internal class ObjectMapperAwareBeanPostProcessor(private val applicationContext: ApplicationContext) : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        if (bean is ObjectMapperAware) {
            bean.setObjectMapper(getObjectMapper())
        }
        return bean
    }

    private fun getObjectMapper(): ObjectMapper {
        return SingletonSupplier.of {
            applicationContext.getBean(ObjectMapper::class.java)
        }.obtain()
    }

}