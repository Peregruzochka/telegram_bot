package ru.pereguzochka.telegram_bot.handler.direction_handler.info.school_preparation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.school-preparation")
public class SchoolPreparationAttribute extends BaseAttribute {

}
