package kr.junhyung.mainframe.core.io

import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader

public class PrioritizedResourceLoader(private val classLoader: ClassLoader) : ResourceLoader {

    private val primaryResourceLoader: ResourceLoader = DefaultResourceLoader(classLoader)
    private val secondaryResourceLoaders: MutableList<ResourceLoader> = mutableListOf()

    public fun add(resourceLoader: ResourceLoader) {
        this.secondaryResourceLoaders.add(resourceLoader)
    }

    public fun add(classLoader: ClassLoader) {
        this.add(createResourceLoader(classLoader))
    }

    private fun createResourceLoader(classLoader: ClassLoader): ResourceLoader {
        return DefaultResourceLoader(classLoader)
    }

    override fun getResource(location: String): Resource {
        val resource = primaryResourceLoader.getResource(location)

        if (resource.exists()) {
            return resource
        }

        for (resourceLoader in secondaryResourceLoaders) {
            val secondaryResource = resourceLoader.getResource(location)
            if (secondaryResource.exists()) {
                return secondaryResource
            }
        }
        return primaryResourceLoader.getResource(location)
    }

    override fun getClassLoader(): ClassLoader {
        return classLoader
    }

}