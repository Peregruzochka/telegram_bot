package ru.pereguzochka.telegram_bot.handler.direction_handler.info.group_school_preparation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.group-school-preparation")
public class GroupSchoolPreparationAttribute extends BaseAttribute {

}
