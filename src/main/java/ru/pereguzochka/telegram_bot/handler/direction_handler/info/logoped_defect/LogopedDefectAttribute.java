package ru.pereguzochka.telegram_bot.handler.direction_handler.info.logoped_defect;

import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
public class LogopedDefectAttribute extends BaseAttribute<LogopedDefectPojo> {

    public LogopedDefectAttribute(LogopedDefectPojo attributePojo) {
        super(attributePojo);
    }
}
