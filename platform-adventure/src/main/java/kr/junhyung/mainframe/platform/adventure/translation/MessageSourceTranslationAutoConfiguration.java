package kr.junhyung.mainframe.platform.adventure.translation;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.Translator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(Translator.class)
public class MessageSourceTranslationAutoConfiguration {

    private static final Key TRANSLATOR_NAME = Key.key("mainframe", "messages");

    @Bean
    public MessageSourceTranslator messageSourceTranslator(MessageSource messageSource) {
        return new MessageSourceTranslator(TRANSLATOR_NAME, messageSource);
    }

    @Bean
    public GlobalTranslatorRegistrar globalTranslatorRegistrar(MessageSourceTranslator messageSourceTranslator) {
        return new GlobalTranslatorRegistrar(messageSourceTranslator);
    }

}
