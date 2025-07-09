package kr.junhyung.mainframe.paper

import kr.junhyung.mainframe.core.MainframeApplicationCustomizer
import kr.junhyung.mainframe.core.io.PrioritizedResourceLoader
import kr.junhyung.mainframe.core.util.ClassLoaderUtils
import kr.junhyung.mainframe.core.util.stdlib.uncheckedCast
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.springframework.boot.ApplicationContextFactory
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.ResolvableType
import org.springframework.core.io.ResourceLoader
import kotlin.jvm.java

@Suppress("unused")
public abstract class PaperMainframePlugin<T> : JavaPlugin(), ApplicationContextFactory,
    MainframeApplicationCustomizer<PaperPluginApplicationContext> {

    private lateinit var applicationContext: ConfigurableApplicationContext

    final override fun getApplicationContext(): PaperPluginApplicationContext? {
        if (!this::applicationContext.isInitialized) {
            return null
        }
        return this.applicationContext.uncheckedCast()
    }

    final override fun create(webApplicationType: WebApplicationType?): ConfigurableApplicationContext {
        val context = PaperPluginApplicationContext(this)
        context.classLoader = this.classLoader
        val resourceLoader = createResourceLoader()
        context.setResourceLoader(resourceLoader)
        context.apply(this::customizeContext)
        return context
    }

    @Suppress("UnstableApiUsage")
    private fun createResourceLoader(): ResourceLoader {
        val resourceLoader = PrioritizedResourceLoader(this.classLoader)
        (pluginMeta.pluginDependencies + pluginMeta.pluginSoftDependencies).map(Bukkit.getPluginManager()::getPlugin)
            .forEach { plugin ->
                requireNotNull(plugin) { "Plugin ${pluginMeta.pluginDependencies} is not loaded" }
                resourceLoader.add(plugin.javaClass.classLoader)
            }
        return resourceLoader
    }

    final override fun onEnable() {
        ClassLoaderUtils.withClassLoader(this.classLoader, ::startApplication)
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

    public open fun onApplicationPreClose() {
    }

    final override fun onDisable() {
        this.onApplicationPreClose()
        if (this::applicationContext.isInitialized) {
            this.applicationContext.close()
        }
    }

    private fun determineMainClass(): Class<*> {
        return ResolvableType
            .forClass(this::class.java)
            .`as`(PaperMainframePlugin::class.java)
            .generics
            .single()
            .resolve() ?: error("Unable to resolve plugin class")
    }

}