package ru.pereguzochka.telegram_bot.handler.start;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.first-start")
public class FirstStartAttribute extends BaseAttribute {

}
