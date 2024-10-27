package ru.pereguzochka.telegram_bot.handler.direction_handler.info.creature_workshop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.creative-workshop")
public class CreativeWorkshopAttribute extends BaseAttribute {
}
