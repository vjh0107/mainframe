package kr.junhyung.mainframe.velocity

import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.proxy.ProxyServer
import kr.junhyung.mainframe.core.MainframeApplicationContext
import kr.junhyung.mainframe.velocity.event.SubscribeAnnotationProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import java.io.File

public class VelocityPluginApplicationContext(
    public val proxyServer: ProxyServer,
    public val pluginContainer: PluginContainer,
    dataDirectory: File
) : MainframeApplicationContext(dataDirectory) {

    override fun onPostProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        registerSubscribeAnnotationProcessor(beanFactory)
    }

    private fun registerSubscribeAnnotationProcessor(beanFactory: ConfigurableListableBeanFactory) {
        val subscribeAnnotationProcessor = SubscribeAnnotationProcessor(this, pluginContainer, proxyServer.eventManager)
        beanFactory.addBeanPostProcessor(subscribeAnnotationProcessor)
    }

}