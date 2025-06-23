package kr.junhyung.mainframe.velocity

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import kr.junhyung.mainframe.core.MainframeApplicationCustomizer
import kr.junhyung.mainframe.core.io.PrioritizedResourceLoader
import kr.junhyung.mainframe.core.util.ClassLoaderUtils
import kr.junhyung.mainframe.core.util.stdlib.uncheckedCast
import org.slf4j.Logger
import org.springframework.boot.ApplicationContextFactory
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.ResolvableType
import java.nio.file.Path
import javax.inject.Inject
import kotlin.jvm.java

public abstract class MainframePlugin<T> : ApplicationContextFactory, MainframeApplicationCustomizer<VelocityPluginApplicationContext> {

    @Inject
    protected lateinit var proxyServer: ProxyServer
    @Inject
    protected lateinit var pluginContainer: PluginContainer
    @DataDirectory
    @Inject
    protected lateinit var dataDirectory: Path
    @Inject
    public lateinit var logger: Logger

    private lateinit var applicationContext: ConfigurableApplicationContext

    override fun create(webApplicationType: WebApplicationType?): ConfigurableApplicationContext {
        val context = VelocityPluginApplicationContext(proxyServer, pluginContainer, dataDirectory.toFile())
        context.classLoader = this.javaClass.classLoader
        context.apply(this::customizeContext)
        val resourceLoader = PrioritizedResourceLoader(this.javaClass.classLoader)
        pluginContainer.description.dependencies.forEach { dependency ->
            val dependencyPlugin = proxyServer.pluginManager.getPlugin(dependency.id)
            val classLoader = dependencyPlugin.get().instance.javaClass.classLoader
            resourceLoader.add(classLoader)
        }
        context.setResourceLoader(resourceLoader)
        return context
    }

    @Subscribe
    internal fun onProxyInitialize(event: ProxyInitializeEvent) {
        require(this::proxyServer.isInitialized) { "ProxyServer is not injected" }
        require(this::pluginContainer.isInitialized) { "PluginContainer is not injected" }
        require(this::dataDirectory.isInitialized) { "DataDirectory is not injected" }
        ClassLoaderUtils.withClassLoader(this.javaClass.classLoader, ::startApplication)
    }

    private fun startApplication() {
        onApplicationStart()
        val context = SpringApplicationBuilder()
            .sources(determineMainClass())
            .contextFactory(this)
            .headless(true)
            .registerShutdownHook(false)
            .bannerMode(Banner.Mode.LOG)
            .let(this::customizeBuilder)
            .run()
        this.applicationContext = context
        onApplicationStarted(context.uncheckedCast())
    }

    @Subscribe
    internal fun onProxyShutdown(event: ProxyShutdownEvent) {
        if (this::applicationContext.isInitialized) {
            this.applicationContext.close()
        }
    }

    private fun determineMainClass(): Class<*> {
        return ResolvableType
            .forClass(this::class.java)
            .`as`(MainframePlugin::class.java)
            .generics
            .single()
            .resolve() ?: error("Unable to resolve plugin class")
    }

}