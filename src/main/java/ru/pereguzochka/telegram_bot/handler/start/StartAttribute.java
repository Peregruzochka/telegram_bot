package ru.pereguzochka.telegram_bot.handler.start;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.start")
public class StartAttribute extends BaseAttribute {

    public String createText(String name) {
        return text.replace("{}", name);
    }
}
