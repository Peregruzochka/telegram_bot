package ru.pereguzochka.telegram_bot.handler.direction_handler.info.calligraphy;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.calligraphy")
public class CalligraphyAttribute extends BaseAttribute {

}
