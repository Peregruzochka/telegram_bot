package ru.pereguzochka.telegram_bot.handler.direction_handler.info.logoped;

import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
public class LogopedAttribute extends BaseAttribute<LogopedInfoPojo> {

    public LogopedAttribute(LogopedInfoPojo attributePojo) {
        super(attributePojo);
    }
}
