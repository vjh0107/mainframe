package kr.junhyung.mainframe.platform.adventure.translation;

import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class GlobalTranslatorRegistrar implements InitializingBean, DisposableBean {

    private final Translator translator;

    public GlobalTranslatorRegistrar(Translator translator) {
        this.translator = translator;
    }

    @Override
    public void afterPropertiesSet() {
        GlobalTranslator.translator().addSource(translator);
    }

    @Override
    public void destroy() {
        GlobalTranslator.translator().removeSource(translator);
    }

}
