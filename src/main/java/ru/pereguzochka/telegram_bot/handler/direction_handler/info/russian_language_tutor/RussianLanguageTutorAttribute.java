package ru.pereguzochka.telegram_bot.handler.direction_handler.info.russian_language_tutor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.russian-language-tutor")
public class RussianLanguageTutorAttribute extends BaseAttribute {
}
