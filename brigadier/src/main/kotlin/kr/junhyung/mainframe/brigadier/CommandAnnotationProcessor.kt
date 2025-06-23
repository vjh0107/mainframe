package kr.junhyung.mainframe.brigadier

import kr.junhyung.mainframe.core.annotation.ClassAnnotationProcessor

public class CommandAnnotationProcessor(
    private val registrar: CommandRegistrar,
) : ClassAnnotationProcessor<Command>() {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String, annotation: Command) {
        registrar.registerCommand(bean, annotation)
        if (bean is CommandAnnotatedElement) {
            bean.setCommand(annotation)
        }
    }

}