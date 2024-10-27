package ru.pereguzochka.telegram_bot.handler.direction_handler.info.logopedic_massage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.logoped-massage")
public class LogopedMassageAttribute extends BaseAttribute {

}
