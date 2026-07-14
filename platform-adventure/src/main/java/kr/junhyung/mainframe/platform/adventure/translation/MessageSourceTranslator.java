package kr.junhyung.mainframe.platform.adventure.translation;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.Translator;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;

import java.text.MessageFormat;
import java.util.Locale;

public final class MessageSourceTranslator implements Translator {

    private final Key name;
    private final MessageSource messageSource;

    public MessageSourceTranslator(Key name, MessageSource messageSource) {
        this.name = name;
        this.messageSource = messageSource;
    }

    @Override
    public @NonNull Key name() {
        return name;
    }

    @Override
    public MessageFormat translate(@NonNull String key, @NonNull Locale locale) {
        String pattern = messageSource.getMessage(key, null, null, locale);
        if (pattern == null) {
            return null;
        }
        return new MessageFormat(pattern, locale);
    }

}
