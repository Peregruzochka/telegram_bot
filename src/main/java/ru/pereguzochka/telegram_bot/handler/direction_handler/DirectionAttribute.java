package ru.pereguzochka.telegram_bot.handler.direction_handler;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.direction")
public class DirectionAttribute extends BaseAttribute {

}
