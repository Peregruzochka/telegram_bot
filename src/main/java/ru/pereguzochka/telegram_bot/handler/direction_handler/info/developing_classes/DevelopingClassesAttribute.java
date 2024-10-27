package ru.pereguzochka.telegram_bot.handler.direction_handler.info.developing_classes;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.developing-classes")
public class DevelopingClassesAttribute extends BaseAttribute {

}
