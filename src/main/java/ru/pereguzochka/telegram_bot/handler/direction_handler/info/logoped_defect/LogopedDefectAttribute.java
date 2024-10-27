package ru.pereguzochka.telegram_bot.handler.direction_handler.info.logoped_defect;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.logoped-defect")
public class LogopedDefectAttribute extends BaseAttribute {

}
