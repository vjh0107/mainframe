package kr.junhyung.mainframe.gradle

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

public abstract class MainframeExtension @Inject constructor(
    objects: ObjectFactory,
) {

    internal companion object {
        const val NAME: String = "mainframe"
    }

    public val enableShadowJar: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

    public fun shadowJar() {
        enableShadowJar.set(true)
    }

}