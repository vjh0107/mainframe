package kr.junhyung.mainframe.paper

import kr.junhyung.mainframe.core.MainframeApplicationContext
import kr.junhyung.mainframe.paper.event.EventHandlerAnnotationProcessor
import kr.junhyung.mainframe.paper.service.ServiceAnnotationProcessor
import org.bukkit.plugin.Plugin
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory

public class PaperPluginApplicationContext(private val plugin: Plugin) : MainframeApplicationContext(plugin.dataFolder) {

    override fun onPostProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        registerPluginBean(beanFactory)
        registerEventHandlerAnnotationProcessor(beanFactory)
        registerServiceAnnotationProcessor(beanFactory)
    }

    private fun registerEventHandlerAnnotationProcessor(beanFactory: ConfigurableListableBeanFactory) {
        val eventHandlerAnnotationProcessor = EventHandlerAnnotationProcessor(plugin)
        beanFactory.addBeanPostProcessor(eventHandlerAnnotationProcessor)
    }

    private fun registerServiceAnnotationProcessor(beanFactory: ConfigurableListableBeanFactory) {
        val serviceAnnotationProcessor = ServiceAnnotationProcessor(plugin)
        beanFactory.addBeanPostProcessor(serviceAnnotationProcessor)
    }

    private fun registerPluginBean(beanFactory: ConfigurableListableBeanFactory) {
        beanFactory.registerSingleton(plugin.name, plugin)
    }

}