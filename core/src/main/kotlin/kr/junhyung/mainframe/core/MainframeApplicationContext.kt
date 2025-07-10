package kr.junhyung.mainframe.core

import kr.junhyung.mainframe.core.coroutines.CoroutineScopeAwareBeanPostProcessor
import kr.junhyung.mainframe.core.jackson.ObjectMapperAwareBeanPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.env.YamlPropertySourceLoader
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.io.FileSystemResource
import java.io.File

public abstract class MainframeApplicationContext(private val dataDirectory: File) : GenericApplicationContext() {

    private val yamlPropertySourceLoader = YamlPropertySourceLoader()

    public abstract fun onPostProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory)

    public override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        registerJacksonBeans(beanFactory)
        registerCoroutineBeans(beanFactory)

        onPostProcessBeanFactory(beanFactory)
    }

    final override fun prepareRefresh() {
        if (getResourceByPath("application.yml").exists()) {
            installConfiguration("application.yml")
        }
        super.prepareRefresh()
    }

    private fun registerJacksonBeans(beanFactory: ConfigurableListableBeanFactory) {
        beanFactory.addBeanPostProcessor(ObjectMapperAwareBeanPostProcessor(this))
    }

    private fun registerCoroutineBeans(beanFactory: ConfigurableListableBeanFactory) {
        beanFactory.addBeanPostProcessor(CoroutineScopeAwareBeanPostProcessor(this))
    }

    public fun installConfiguration(configurationFileName: String) {
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs()
        }
        val preset = getResourceByPath(configurationFileName)
        if (!preset.exists()) {
            error("Preset file not found: ${preset.description}")
        }
        val destination = dataDirectory.resolve(configurationFileName)
        if (!destination.exists()) {
            preset.inputStream.use { input ->
                destination.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        loadYamlProperties(destination)
    }

    public fun loadYamlProperties(yamlFile: File) {
        val resource = FileSystemResource(yamlFile)
        yamlPropertySourceLoader.load(resource.filename, resource).forEach { propertySource ->
            environment.propertySources.addLast(propertySource)
        }
    }

}